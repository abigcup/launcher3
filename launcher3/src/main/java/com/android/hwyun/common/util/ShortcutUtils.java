package com.android.hwyun.common.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.IntentUtils;

import java.io.ByteArrayOutputStream;

/**
 *   这里的代码更可复用些，不含其它业务结构代码（提供给 上传管理.apk）
 */
// https://blog.csdn.net/yingaizhu/article/details/79699880?tdsourcetag=s_pctim_aiomsg
public class ShortcutUtils {
    /**
     * 添加桌面图标快捷方式
     *
     * @param activity     Activity对象
     * @param name         快捷方式名称
     * @param icon         快捷方式图标
     * @param actionIntent 快捷方式图标点击动作
     */
    public static void addShortcut(Context activity, String name, Bitmap icon, Intent actionIntent) {
        Log.e("ShortcutUtils", "addShortcut: " + name + " " + actionIntent + " size: " + (icon.getRowBytes() * icon.getHeight()));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {// 7.0 API 24
            //  创建快捷方式的intent广播
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 添加快捷名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            //  快捷图标是允许重复(不一定有效)
            shortcut.putExtra("duplicate", false);
            // 快捷图标
            // 使用资源id方式
//            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(activity, R.mipmap.icon);
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            // 使用Bitmap对象模式
            // sendBroadcast RuntimeException TransactionTooLargeException data parcel size 1049496 > 1M bytes
//            Bitmap bitmap = ImageUtils.compressBySampleSize(icon, 64, 64);
            Bitmap bitmap = compressBySampleSize(icon, 64, 64, false);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
            // 添加携带的下次启动要用的Intent信息
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            // 发送广播
            activity.sendBroadcast(shortcut);
        } else {
            ShortcutManager shortcutManager = (ShortcutManager) activity.getSystemService(Context.SHORTCUT_SERVICE);
            if (null == shortcutManager) {
                // 创建快捷方式失败
                Log.e("MainActivity", "Create shortcut failed");
                return;
            }
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(activity, name)
                    .setShortLabel(name)
                    .setIcon(Icon.createWithBitmap(icon))
                    .setIntent(actionIntent)
                    .setLongLabel(name)
                    .build();
//            shortcutManager.requestPinShortcut(shortcutInfo, PendingIntent.getActivity(activity,
//                    RC_CREATE_SHORTCUT, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT).getIntentSender());
        }
    }



    /**
     * 应用名
     *
     * @param packageName
     */
    public static void addShortcut(Context activity,  String packageName) {
        AppUtils.AppInfo appInfo = AppUtils.getAppInfo(packageName);
        Intent launchAppIntent = IntentUtils.getLaunchAppIntent(packageName);

        if (launchAppIntent.getComponent() != null) {
            Intent intent = new Intent(/*Intent.ACTION_MAIN*/);
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName comp = new ComponentName(launchAppIntent.getComponent().getPackageName(), launchAppIntent.getComponent().getClassName());
            intent.setComponent(comp);
            addShortcut(activity, appInfo.getName(), ((BitmapDrawable) appInfo.getIcon()).getBitmap(), intent);
        } else {
            addShortcut(activity, appInfo.getName(), ((BitmapDrawable) appInfo.getIcon()).getBitmap(), launchAppIntent);
        }
    }

    private static boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    /**
     * Return the compressed bitmap using sample size.
     *
     * @param src       The source of bitmap.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @param recycle   True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int maxWidth,
                                              final int maxHeight,
                                              final boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    /**
     * Return the sample size.
     *
     * @param options   The options.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the sample size
     */
    private static int calculateInSampleSize(final BitmapFactory.Options options,
                                             final int maxWidth,
                                             final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while ((width >>= 1) >= maxWidth && (height >>= 1) >= maxHeight) {
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }
}
