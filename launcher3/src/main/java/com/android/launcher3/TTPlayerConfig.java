package com.android.launcher3;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;


/**
 * Created by HuangJie on 2017/12/5.
 */

public class TTPlayerConfig {

    static String TTPLAYER_SP_NAME = "TTPLAYER_SP_NAME";
    static String WALL_PAPER = "wallpaper";

    static  TTPlayerConfig INSTANCE = new TTPlayerConfig();

    public static TTPlayerConfig getInstance(){
        return INSTANCE;
    }

    public void setWallPaper(Context context,String wallPaper){
        SharedPreferences sp = context.getSharedPreferences(TTPLAYER_SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(WALL_PAPER, wallPaper).apply();
    }

    public String getWallPaper(Context context){
        SharedPreferences sp = context.getSharedPreferences(TTPLAYER_SP_NAME, Context.MODE_PRIVATE);
        String wallPaper = sp.getString(WALL_PAPER,"");
        return wallPaper;
    }
}
