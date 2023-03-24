package com.android.launcher3;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.launcher3.Application.MainApplication;

import java.io.IOException;


/**
 * Created by HuangJie on 2017/12/5.
 */

public class TTPlayerReceiver extends BroadcastReceiver {

    public static String ACTION_SET_WALL_PAPER = "tiantianplayer.intent.action.SET_WALL_PAPER";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ACTION_SET_WALL_PAPER)){
            final String wallPaper = intent.getStringExtra("wallpaper");
            TTPlayerConfig.getInstance().setWallPaper(context, wallPaper);
            final WallpaperManager wm = WallpaperManager.getInstance(MainApplication.getContext());
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(wallPaper);
                if (bitmap != null){
                    wm.setBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
