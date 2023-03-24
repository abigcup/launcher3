package com.android.hwyun.statistics;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.android.Utils.ShellUtils2;
import com.android.hwyun.common.bean.RequestBase;
import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.PhoneIDUtil;
import com.android.hwyun.common.util.UserInfoUtil;
import com.android.launcher3.Application.MainApplication;
import com.android.launcher3.BuildConfig;
import com.android.launcher3.R;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lijingying on 2019/3/26.
 * http://app.ddyun123.com/Help/Api/POST-AppMarket-CloudDeviceProcessDataCollect
 */
public class CloudDeviceProcessDataCollectModel {

    private Handler handler = new Handler();
    private Runnable runnable = null;
    private ActivityHttpHelper<BaseResultWrapper<String>> collectHttpHelper;
    private ActivityHttpHelper<BaseResultWrapper<List<String>>> filterHttpHelper;

    private Set<String> systemPackages;

    private RequestData request = new RequestData();

    private static class ProcessAppInfoBean {
        /**
         * AppName : sample string 1
         * AppPackageName : sample string 2
         */

        private String AppName;
        private String AppPackageName;
        private int Type; //类型 0-后台应用 1-前台应用

        public String getAppName() {
            return AppName;
        }

        public void setAppName(String AppName) {
            this.AppName = AppName;
        }

        public String getAppPackageName() {
            return AppPackageName;
        }

        public void setAppPackageName(String AppPackageName) {
            this.AppPackageName = AppPackageName;
        }

        public int getType() {
            return Type;
        }

        public void setType(int type) {
            Type = type;
        }
    }

    private static class RequestData extends RequestBase {

        /**
         * ProcessAppInfo : [{"AppName":"sample string 1","AppPackageName":"sample string 2"},{"AppName":"sample string 1","AppPackageName":"sample string 2"}]
         * PhoneId : sample string 1
         * OrderId : 2
         */

        private String PhoneId;
        private long OrderId;
        private List<ProcessAppInfoBean> ProcessAppInfo;

        public boolean init() {
            PhoneId = PhoneIDUtil.getInstance().getPhoneID();
            OrderId = UserInfoUtil.getOrderID();

            Log.i("statistics", "statisticsProcessData init PhoneId=" + PhoneId + ",OrderId=" + OrderId);
            return !PhoneId.isEmpty();
        }

        public String getPhoneId() {
            return PhoneId;
        }

        public void setPhoneId(String PhoneId) {
            this.PhoneId = PhoneId;
        }

        public long getOrderId() {
            return OrderId;
        }

        public void setOrderId(long OrderId) {
            this.OrderId = OrderId;
        }

        public List<ProcessAppInfoBean> getProcessAppInfo() {
            return ProcessAppInfo;
        }

        public void setProcessAppInfo(List<ProcessAppInfoBean> ProcessAppInfo) {
            this.ProcessAppInfo = ProcessAppInfo;
        }
    }

    /**
     * 后台进程统计
     */
    private void statisticsProcessData(final List<ProcessAppInfoBean> beans) {
        try {
            if (collectHttpHelper == null) {
                TypeToken<BaseResultWrapper<String>> typeToken = new TypeToken<BaseResultWrapper<String>>() {
                };
                collectHttpHelper = new ActivityHttpHelper<>(new IUIDataListener() {
                    @Override
                    public void uiDataSuccess(Object object) {
                        Log.i("statistics", "statisticsProcessData uiDataSuccess");
                    }

                    @Override
                    public void uiDataError(Exception error) {
                        Log.e("statistics", "statisticsProcessData uiDataError");
                    }
                }, typeToken);
            }

            RequestData request = new RequestData();
            request.init();
            request.setProcessAppInfo(beans);

            for (ProcessAppInfoBean b : beans) {
                Log.i("statistics", "statisticsProcessData send " + b.getAppPackageName());
            }

            Log.i("statistics", "statisticsProcessData beans.size " + beans.size());
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            collectHttpHelper.sendPostRequest(HttpConstants.APPMARKET_CLOUND_DEVICE_PROCESS_DATA_COLLECT, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            Log.e("statistics", "statisticsProcessData Exception " + e.getMessage());
        }
    }

    /**
     * 获取进程过滤列表（根据包名过滤）
     */
    private void requestFilterList() {
        try {
            if (filterHttpHelper == null) {
                TypeToken<BaseResultWrapper<List<String>>> typeToken = new TypeToken<BaseResultWrapper<List<String>>>() {
                };
                filterHttpHelper = new ActivityHttpHelper<>(new IUIDataListener() {
                    @Override
                    public void uiDataSuccess(Object object) {
                        BaseResultWrapper<List<String>> resultWrapper = (BaseResultWrapper<List<String>>) object;
                        if (resultWrapper != null) {
                            if (resultWrapper.code == 1) {//请求成功
                                Log.i("statistics", "requestFilterList uiDataSuccess:\n" + resultWrapper.data);
                                tryStatisticsProcessData(resultWrapper.data);
                            } else {
                                Log.e(
                                        "statistics",
                                        String.format("requestFilterList net error.code:%s,msg:%s", resultWrapper.code, resultWrapper.msg)
                                );
                                tryStatisticsProcessData(null);
                            }
                        } else {
                            Log.e("statistics", "requestFilterList parser error");
                            tryStatisticsProcessData(null);
                        }
                    }

                    @Override
                    public void uiDataError(Exception error) {
                        tryStatisticsProcessData(null);
                        Log.e("statistics", "requestFilterList uiDataError:" + error.getMessage());
                    }
                }, typeToken);
            }

            RequestBase request = new RequestBase();
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            filterHttpHelper.sendPostRequest(HttpConstants.APPMARKET_CLOUND_REQUEST_FILTER_WORD, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            Log.e("statistics", "statisticsProcessData Exception " + e.getMessage());
        }
    }

    /**
     * @param psInfo 命令ps获取到的内容
     * @return
     */
    private Set<String> getPackagesByPsInfo(String psInfo) {
        Set<String> packages = new HashSet<>();

        String rows[] = psInfo.split("\n");
        for (String row : rows) {
//            root      1     0     9940   2108  SyS_epoll_ 00004c7e90 S /init
//            root      2     0     0      0       kthreadd 0000000000 S kthreadd
            int index = row.lastIndexOf(' ');
            if (index < 0) {
                continue;
            }

            String p = row.substring(index).trim();
            packages.add(p);
        }

        return packages;
    }

    /**
     * @param filterPackageListFromNet 从服务器返回的包名列表 显示全部进程命令Android7【ps】，Android9【ps -A】
     */
    private void tryStatisticsProcessData(List<String> filterPackageListFromNet) {
        ShellUtils2.CommandResult result = Build.VERSION.SDK_INT == 25 ? ShellUtils2.execCmd("ps", true) : ShellUtils2.execCmd("ps -A", true);
        if (!result.successMsg.isEmpty()) {
            Set<String> psPackages = getPackagesByPsInfo(result.successMsg);

            Log.i("statistics", "autoStatisticsProcessData s=" + systemPackages.size() + ",p=" + psPackages.size());
            Log.e("statistics", "all packageName:" + psPackages.toString());
            Set<String> filterSet = new HashSet<>();
            if (systemPackages != null && !systemPackages.isEmpty()) {
                filterSet.addAll(systemPackages);//过滤内置资源的包名
            }
            if (filterPackageListFromNet != null && !filterPackageListFromNet.isEmpty()) {
                filterSet.addAll(filterPackageListFromNet);//过滤网络返回的包名
            }

            for (String s : filterSet) {
                for (String p : psPackages) {
                    if (p.equals(s)) {
                        psPackages.remove(p);
                        break;
                    }
                }
            }

            Log.e("statistics", "filter packageName form resource:" + (systemPackages != null ? systemPackages.toString() : "[]"));
            Log.e("statistics", "filter packageName from net:" + (filterPackageListFromNet != null ? filterPackageListFromNet.toString() : "[]"));
            Log.e("statistics", "packageName filtered:" + psPackages.toString());

            List<ProcessAppInfoBean> beans = new ArrayList<>();
            for (String p : psPackages) {
                if (!p.contains(".")) {
                    continue;
                }

                String name = "";
                try {
                    name = AppUtils.getAppName(p);
                }catch (Exception ex){
                    Log.e("statistics", "autoStatisticsProcessData exception " + p);
                    ex.printStackTrace();
                }

                if (name.isEmpty()) {//极大概率是系统进程或其它二进制进程
                    continue;
                }

                ProcessAppInfoBean bean = new ProcessAppInfoBean();
                bean.setAppPackageName(p);
                bean.setAppName(name);
                beans.add(bean);
            }

            if (!beans.isEmpty()) {
                //判断谁是在前台
                String top = getTopApp5();
                Log.i("statistics", "autoStatisticsProcessData top:" + top);
                if (!top.isEmpty()) {
                    for (ProcessAppInfoBean bean : beans) {
                        if (top.equals(bean.getAppPackageName())) {
                            bean.setType(1);
                            Log.i("statistics", "autoStatisticsProcessData AppForeground:" + top);
                            break;
                        }
                    }
                }
                statisticsProcessData(beans);
            } else {
                ProcessAppInfoBean bean = new ProcessAppInfoBean();
                bean.setAppPackageName("com.null");
                bean.setAppName("null");
                beans.add(bean);

                statisticsProcessData(beans);
            }
        }
    }

    /**
     * 定时1小时，统计一次数据。并如实上报（去重原系统拥有的进程）
     */
    public void autoStatisticsProcessData() {
        //分析备份的系统ps进程（到时需要过滤掉）
        String systemPs = ResourceUtils.readRaw2String(R.raw.systemps);
        systemPackages = getPackagesByPsInfo(systemPs);
        Log.i("statistics", String.format("systemPackages.size %d", systemPackages.size()));

        //开始定时
        int times = 1 * 60 * 60 * 1000;

        //如果配置文件有配置，则缩短到15分钟
        try {
            if (FileUtils.isFile("/data/local/setting/autoStatisticsProcessData.txt")) {
                String sTimes = FileIOUtils.readFile2String("/data/local/setting/autoStatisticsProcessData.txt");
                Log.i("statistics", String.format("autoStatisticsProcessData.txt %s", sTimes));
                times = Integer.valueOf(sTimes).intValue();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            times = 1 * 60 * 1000;
        }
        final int mTimes = times;

        runnable = new Runnable() {

            @Override
            public void run() {
                if (request.init()
                        || BuildConfig.DEBUG) {
                    requestFilterList();
                } else
                    Log.e("statistics", "request.init false");

                Log.i("statistics", String.format("postDelayed %dms", mTimes));

                handler.postDelayed(this, mTimes);
            }
        };

        Log.i("statistics", String.format("postDelayed %dms", 60 * 1000));
        handler.postDelayed(runnable, 60 * 1000);
    }

    public void stopStatisticsProcessData() {
        handler.removeCallbacks(runnable);
    }

    private static String getTopApp5() {
        long l = System.currentTimeMillis();

        String packageName = "";
        UsageStatsManager usageStatsManager = (UsageStatsManager) MainApplication.getInstance().getSystemService("usagestats"/*Context.USAGE_STATS_SERVICE*/);
        long ts = System.currentTimeMillis();
        long beginTime = ts - 86400_000;//24 * 60 * 60 * 1000;
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return "";
        }
        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                if (usageStats != null && "com.sohu.inputmethod.sogou".equals(usageStats.getPackageName())) {
                    continue;
                }
                recentStats = usageStats;
            }
        }
        if (recentStats != null) {
            packageName = recentStats.getPackageName();
        }

        return packageName;
    }
}
