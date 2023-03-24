package com.android.hwyun.installrecommend.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.installrecommend.bean.response.AssociatedAppsResponse;
import com.android.hwyun.installrecommend.bean.response.RecommendAppsResponse;
import com.android.hwyun.installrecommend.dialog.AssociatedAppsDialog;
import com.android.hwyun.installrecommend.dialog.RecommendAppsDialog;
import com.android.hwyun.installrecommend.dialog.RecommendMsgDialog;

/**
 * Created by xuwei on 2019/1/8.
 */
public class DialogService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AssociatedAppsResponse response = intent.getParcelableExtra(CommonConstants.EXTRA_DIALOG_APP_RECOMMEND);
        RecommendAppsResponse toastDialog = intent.getParcelableExtra(CommonConstants.EXTRA_DIALOG_TOAST_APP_RECOMMEND);
        if (response != null) {
            AssociatedAppsDialog.showDialog(this, response);
        } else if (toastDialog != null) {
            RecommendAppsDialog.showDialog(this, toastDialog);
        }
        return START_NOT_STICKY;
    }

    /**
     * 全局显示对话框
     */
    public static void showAssociatedDialog(Context context, AssociatedAppsResponse response) {
        Intent startIntent = new Intent(context, DialogService.class);
        startIntent.putExtra(CommonConstants.EXTRA_DIALOG_APP_RECOMMEND, response);
        context.startService(startIntent);
    }
    /**
     * 全局显示推荐消息
     */
    public static void showRecommendDialog(Context context, RecommendAppsResponse response) {
        Intent startIntent = new Intent(context, DialogService.class);
        startIntent.putExtra(CommonConstants.EXTRA_DIALOG_TOAST_APP_RECOMMEND, response);
        context.startService(startIntent);
    }
}
