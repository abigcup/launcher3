/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;

import com.android.hwyun.common.util.UserInfoUtil;
import com.android.launcher3.bean.InstallShortCutInfo;
import com.android.launcher3.bean.ShortCutParam;
import com.android.launcher3.util.JsonUtil;
import com.android.launcher3.util.WorkUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ImageUtils;

public class ShortcutDropTarget extends ButtonDropTarget {

    public ShortcutDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShortcutDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // Get the hover color
        mHoverColor = getResources().getColor(R.color.info_target_hover_tint);

        setDrawable(R.drawable.ic_shortcut_launcher);
    }

    @Override
    public void onDrop(DragObject d) {
        if (d.dragSource instanceof UninstallDropTarget.UninstallSource) {
            ((UninstallDropTarget.UninstallSource) d.dragSource).deferCompleteDropAfterUninstallActivity();
        }
        super.onDrop(d);
    }

    public static void startInstallShortcut(Object info, Launcher launcher) {
        ComponentName componentName = null;
        Bitmap bitmap = null;
        CharSequence appName = "";
        if (info instanceof AppInfo) {
            componentName = ((AppInfo) info).componentName;
            bitmap = ((AppInfo) info).iconBitmap;
            appName = ((AppInfo) info).title;
        } else if (info instanceof ShortcutInfo) {
            componentName = ((ShortcutInfo) info).intent.getComponent();
            bitmap = ((ShortcutInfo) info).getIcon(LauncherAppState.getInstance().getIconCache());
            appName = ((ShortcutInfo) info).title;
        }
        if (componentName != null && bitmap != null) {
            ShortCutParam shortCutParam = new ShortCutParam();
            shortCutParam.packageName = componentName.getPackageName();
            shortCutParam.activityName = info instanceof ShortcutInfo ? componentName.getClassName() : "";
            shortCutParam.OrderId = UserInfoUtil.getOrderID();
            InstallShortCutInfo installShortCutInfo = new InstallShortCutInfo();
            installShortCutInfo.shortCutParam = shortCutParam;
            installShortCutInfo.appName = TextUtils.isEmpty(appName) ? AppUtils.getAppName(componentName.getPackageName()) : appName.toString();
            byte[] byteArray = ImageUtils.bitmap2Bytes(bitmap, Bitmap.CompressFormat.PNG);
            installShortCutInfo.icon = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            WorkUtils.sendToClient("installShortcut", JsonUtil.objectToString(installShortCutInfo));
        }
    }

    @Override
    protected boolean supportsDrop(DragSource source, Object info) {
        return supportsDrop(getContext(), info);
    }

    public static boolean supportsDrop(Context context, Object info) {
        //只有支持的安卓客户端才显示
        /*if (!TextUtils.equals(UserInfoUtil.getComment(), "android") || UserInfoUtil.getDDYVercode() < 1450 || SettingConfig.getInstance().isNoRealShortCut()) {
            return false;
        }
        if (info instanceof ShortcutInfo || info instanceof AppInfo) {
            return ((ItemInfo)info).itemType != LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT;
        }*/
        return false;// 2.2.5 取消创建真机快捷方式功能
    }

    @Override
    void completeDrop(DragObject d) {
        startInstallShortcut(d.dragInfo, mLauncher);
        DragSource target = d.dragSource;
        if (target instanceof UninstallDropTarget.UninstallSource) {
            ((UninstallDropTarget.UninstallSource) target).onUninstallActivityReturned(false);
        }
    }
}
