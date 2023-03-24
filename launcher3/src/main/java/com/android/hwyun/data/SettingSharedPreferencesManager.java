package com.android.hwyun.data;

import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SettingSharedPreferencesManager {



    private static final SettingSharedPreferencesManager instance = new SettingSharedPreferencesManager();

    /**
     * 私有构造方法
     */
    private SettingSharedPreferencesManager() {
    }

    /**
     * 唯一公开获取实例的方法（静态工厂方法）
     *
     * @return
     */
    public static SettingSharedPreferencesManager getInstance() {
        return instance;
    }



    //public boolean mScreenIsLoop = false; //屏幕是否循环
    public String mScreenIsLoopKey = "screenloop";
    //public boolean mScreenIsLoopWithFirst = false; //屏幕循环是否包括负一屏
    public String mScreenIsLoopWithFirstKey = "screenloopwithfirst";


    public boolean ismScreenIsLoop(Context context) {
        //获取SharedPreferences对象
        SharedPreferences sp = context.getSharedPreferences("LauncherSetting", MODE_MULTI_PROCESS );
        Log.d("cym manager" , sp.getBoolean(mScreenIsLoopKey, false)+"");
        return sp.getBoolean(mScreenIsLoopKey, false);
    }

    public void setmScreenIsLoop(boolean mScreenIsLoop, Context context) {
        //获取SharedPreferences对象
        SharedPreferences sp = context.getSharedPreferences("LauncherSetting", MODE_MULTI_PROCESS );
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(mScreenIsLoopKey, mScreenIsLoop);
        editor.commit();
    }

    public boolean ismScreenIsLoopWithFirst(Context context) {
        SharedPreferences sp = context.getSharedPreferences("LauncherSetting", MODE_MULTI_PROCESS );
        return sp.getBoolean(mScreenIsLoopWithFirstKey, false);
    }

    public void setmScreenIsLoopWithFirst(boolean mScreenIsLoopWithFirst,Context context) {
        //获取SharedPreferences对象
        SharedPreferences sp = context.getSharedPreferences("LauncherSetting", MODE_MULTI_PROCESS );
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(mScreenIsLoopWithFirstKey, mScreenIsLoopWithFirst);
        editor.commit();
    }








}
