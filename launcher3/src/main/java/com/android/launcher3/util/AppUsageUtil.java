package com.android.launcher3.util;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppUsageUtil {

    public  ArrayList<PackageInfo> getAllAppInfo(Context ctx, boolean isFilterSystem) {
        ArrayList<PackageInfo> appBeanList = new ArrayList<>();
        PackageInfo bean = null;

        PackageManager packageManager = ctx.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        for (PackageInfo p : list) {
            bean = p;
            int flags = p.applicationInfo.flags;
            // 判断是否是属于系统的apk
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0&&isFilterSystem) {

            } else {
                appBeanList.add(bean);
            }
        }
        return appBeanList;


    }

    public boolean checkPermission(Context mContext){
        UsageStatsManager usageStatsManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        }

        //设置查询时间范围：
        long currentTime = System.currentTimeMillis();
        long oneWeekAgo = currentTime - 60 * 60 * 1000 * 24 *7;

        //调用 queryUsageStats() 方法：
        List<UsageStats> usageStatsList = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, oneWeekAgo, currentTime);
        }

        Log.d("cym" ,"   size usage "+usageStatsList.size());

        if(usageStatsList.size() == 0){
            return false;
        }else{
            return true;
        }
    }


    public int getLaunchCount(UsageStats usageStats) throws IllegalAccessException {
        Field field = null;
        try {
            field = usageStats.getClass().getDeclaredField("mLaunchCount");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return (int) field.get(usageStats);
    }

    public List<UsageStats>  sortByLaunchCount(List<UsageStats> usageStatsList) throws IllegalAccessException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            usageStatsList.sort(new Comparator<UsageStats>() {
                @Override
                public int compare(UsageStats u1, UsageStats u2) {
                    Integer sex1= null;
                    Integer sex2= null;
                    try {
                        sex1 = getLaunchCount(u1);
                        sex2 = getLaunchCount(u2);
                        Log.d("cym" ,"   size sex1 "+sex1);
                        return  sex1.compareTo(sex2);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        for (int i=0;i<usageStatsList.size();i++){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.i("cym",usageStatsList.get(i).getPackageName()+"  "+getLaunchCount(usageStatsList.get(i)));
            }
        }

        return usageStatsList;

    }




}

