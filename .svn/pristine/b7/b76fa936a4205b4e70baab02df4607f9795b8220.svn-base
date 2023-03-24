package com.android.hwyun.common.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.Utils.ShellUtils2;
import com.android.hwyun.common.constants.CommonConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class AppUtil {

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getVersionName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *  获取application中指定的meta-data
     *  @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     **/
    public static String getAppMetaData(String packageName, String key) {
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(key)) {
            return null;
        }
        String channelNumber = null;
        try {
            PackageManager packageManager = Utils.getApp().getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelNumber = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelNumber;
    }

    public static boolean isFengWoAvailable(int verCode) {
        int ver = AppUtils.getAppVersionCode(CommonConstants.FENGWO_PACKAGE_NAME);
        return AppUtils.isAppInstalled(CommonConstants.FENGWO_PACKAGE_NAME)
                &&  ver > 338
//                && ver >= verCode
                && AppUtil.getAppMetaData(CommonConstants.FENGWO_PACKAGE_NAME, CommonConstants.FENGWO_UMENG_CHANNEL).equals("ddy2");
    }

    public static Intent getFengwoTopicIntent(long topicID) {
        Intent startIntent = new Intent();
        ComponentName componentName = new ComponentName(CommonConstants.FENGWO_PACKAGE_NAME, CommonConstants.FENGWO_TOPIC_ACTIVITY);
        startIntent.setComponent(componentName);
        Bundle bundle = new Bundle();
        bundle.putLong(CommonConstants.FENGWO_TOPIC_ACTIVITY_ID, topicID);
        bundle.putInt(CommonConstants.FENGWO_TOPIC_ACTIVITY_FROM, 1);
        startIntent.putExtras(bundle);
        return startIntent;
    }

    public interface FengwoTopicShotcutCallBack {
        void onAddShortcutSuccess(long topicID, String name);
    }
    public static void setFengwoTopicShotcut(final long topicID, final String name, String imageUrl, final FengwoTopicShotcutCallBack callBack) {
        final Context context= com.blankj.utilcode.util.Utils.getApp();
        Glide.with(context)
                .load(imageUrl)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Intent intent = AppUtil.getFengwoTopicIntent(topicID);
                        ShortcutUtils.addShortcut(context, name, ((BitmapDrawable)resource).getBitmap(), intent);
                        if (callBack != null) {
                            callBack.onAddShortcutSuccess(topicID, name);
                        }
                    }
                });
    }

    public static boolean shellLaunchApp(String packageName, String className) {
        //TODO:未知原因，有出现 梦幻西游官方版，华为版本，原正常的启动流程启，会提示：xxx已停止运行。特意更换为am启动
        ShellUtils2.CommandResult result = ShellUtils2.execCmd(
                String.format("am start -n %s/%s", packageName, className),
                true);
        Log.i("AppUtil", "startAppShortcutOrInfoActivity ShortcutInfo " + packageName + " CommandResult res=" + result.result + ",err=" + result.errorMsg);
        return result.errorMsg.isEmpty();
    }

    /**
     * 应用关联弹窗
     * @return
     */
    public final static String CYJH_ACTION_RECOMMEND_APPS = "cyjh.action.recommend.apps";
    public final static String EXTRA_RECOMMEND_APP_NAME = "app_name";
    public final static String EXTRA_RECOMMEND_APP_ID = "app_id";
    public static void popAssociatedDialog(long channelID, String appName) {
        Intent intent = new Intent(CYJH_ACTION_RECOMMEND_APPS);
        intent.putExtra(EXTRA_RECOMMEND_APP_NAME, appName);
        intent.putExtra(EXTRA_RECOMMEND_APP_ID, channelID);
        Utils.getApp().sendBroadcast(intent);
    }
}
