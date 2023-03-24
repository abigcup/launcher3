package com.android.hwyun.data;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class CardSharedPreferencesManager {

    public String calendar = "calendar";
    public String usageapp = "usageapp";

    //public boolean calendarValue = true;
    //public boolean usageappValue = true;

    public void init(){

    }

    public void setCalendar(boolean isShow, Context context){
        //获取SharedPreferences对象
        SharedPreferences sp = context.getSharedPreferences("CardShared", MODE_PRIVATE);
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(calendar, isShow);
        editor.commit();
    }

    public void setUsageapp(boolean isShow,Context context){
        //获取SharedPreferences对象
        SharedPreferences sp = context.getSharedPreferences("CardShared", MODE_PRIVATE);
        //存入数据
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(usageapp, isShow);
        editor.commit();
    }

    public boolean getCalendar(Context context){
        //获取SharedPreferences对象
        SharedPreferences sp = context.getSharedPreferences("CardShared", MODE_PRIVATE);
        return sp.getBoolean(calendar, false);
        //editor.commit();
    }

    public boolean getUsageapp(Context context){
        //获取SharedPreferences对象
        SharedPreferences sp = context.getSharedPreferences("CardShared", MODE_PRIVATE);
        return sp.getBoolean(usageapp, false);
        //editor.commit();
    }
}
