package com.android.launcher3;

import android.app.WallpaperManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.hwyun.activity.CardManagerActivity;
import com.android.hwyun.data.CardSharedPreferencesManager;
import com.android.launcher3.Application.MainApplication;
import com.android.launcher3.util.AppUsageUtil;
import com.android.launcher3.util.DateUtil;
import com.android.launcher3.util.LunarCalendarFestivalUtils;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FirstScreenView extends RelativeLayout implements View.OnClickListener {

    public Context mContext;


    public FirstScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public FirstScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    Launcher launcher;

    public FirstScreenView(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.first_screen_view, this,
                true);
        launcher = (Launcher) context;

        initView();

    }

    ExtendedEditText searchLayout;

    TextView dateContent1;
    TextView dateContent2;

    ImageView commonUseApp1;
    ImageView commonUseApp2;
    ImageView commonUseApp3;
    ImageView commonUseApp4;

    LinearLayout common_app1;
    LinearLayout common_app2;
    LinearLayout common_app3;
    LinearLayout common_app4;

    LinearLayout calendarLayout;
    LinearLayout commonAppLayout;

    Button mCardManager;


    public void initView(){

        dateContent1 = findViewById(R.id.first_screen_date_content1);
        dateContent2 = findViewById(R.id.first_screen_date_content2);

        searchLayout = findViewById(R.id.search_box_input);
        searchLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.showAppsView(true /* animated */, false /* resetListToTop */,
                        true /* updatePredictedApps */, false /* focusSearchBar */);
            }
        });

        //日期更新
        timer.schedule(timerTask, 0, 100000);

        commonUseApp1 = findViewById(R.id.common_use_app1);
        commonUseApp2 = findViewById(R.id.common_use_app2);
        commonUseApp3 = findViewById(R.id.common_use_app3);
        commonUseApp4 = findViewById(R.id.common_use_app4);

        common_app1 = findViewById(R.id.common_app1);
        common_app2 = findViewById(R.id.common_app2);
        common_app3 = findViewById(R.id.common_app3);
        common_app4 = findViewById(R.id.common_app4);

        calendarLayout = findViewById(R.id.calendar_layout);
        commonAppLayout = findViewById(R.id.common_app_layout);




        MainApplication.fistScreenViewHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1){
                    refreshCard();
                }
            }

        };



        //commonUseApp1.setImageResource(R.mipmap.ic_launcher_home);

        mCardManager = findViewById(R.id.card_manager);
        mCardManager.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(launcher, CardManagerActivity.class);

                launcher.startActivity(intent);
            }
        });

        getUsageApps();
    }


    public void refreshCard(){
        CardSharedPreferencesManager cpm = new CardSharedPreferencesManager();
        if(cpm.getCalendar(mContext)==true){
            calendarLayout.setVisibility(View.VISIBLE);
        }else{
            calendarLayout.setVisibility(View.GONE);
        }

        if(cpm.getUsageapp(mContext)){
            commonAppLayout.setVisibility(View.VISIBLE);
        }else{
            commonAppLayout.setVisibility(View.GONE);
        }
    }

    private final Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            String nowDate = DateUtil.getNowDate();
            String month = nowDate.substring(5,nowDate.length()-3);
            String day = nowDate.substring(8,nowDate.length());
            String content1 = month+"月"+day+"日"+"   "+DateUtil.getWeekOfDate(new Date());
            dateContent1.setText(content1);

            LunarCalendarFestivalUtils festival = new LunarCalendarFestivalUtils();
            festival.initLunarCalendarInfo(DateUtil.getNowDate());

            dateContent2.setText("农历 "+festival.getLunarMonth()+"月"+festival.getLunarDay()+"日");
        }
    };

    // 通过包名获取对应的 Drawable 数据
    private Drawable getAppIcon(String packageName) {
        try {
            PackageManager pm = mContext.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);

            return info.loadIcon(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public void getUsageApps(){

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

        ArrayList<PackageInfo> packages = new AppUsageUtil().getAllAppInfo(mContext,true);
        Log.d("cym" ,"size usage" +packages.size());
        List<UsageStats> usageStatsListWithOutSystem = new ArrayList<UsageStats>();
        int flag = 1;
        for(int j=0;j<packages.size();j++){
            for(int i=0;i<usageStatsList.size();i++){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if(packages.get(j).packageName.equals(usageStatsList.get(i).getPackageName())){
                        usageStatsListWithOutSystem.add(usageStatsList.get(i));
                        if(flag==1)
                            commonUseApp1.setImageDrawable(getAppIcon(usageStatsList.get(i).getPackageName()));
                        if(flag==2)
                            commonUseApp2.setImageDrawable(getAppIcon(usageStatsList.get(i).getPackageName()));
                        if(flag==3)
                            commonUseApp3.setImageDrawable(getAppIcon(usageStatsList.get(i).getPackageName()));
                        if(flag==4)
                            commonUseApp4.setImageDrawable(getAppIcon(usageStatsList.get(i).getPackageName()));
                        flag++;
                        Log.d("cym" ,"   size usage "+usageStatsList.get(i).getPackageName());
                        //break;
                    }
                }
            }
        }

        try {
            new AppUsageUtil().sortByLaunchCount(usageStatsListWithOutSystem);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.common_app1){

        }
        if(view.getId() == R.id.common_app2){

        }
        if(view.getId() == R.id.common_app3){

        }
        if(view.getId() == R.id.common_app4){

        }
    }
}
