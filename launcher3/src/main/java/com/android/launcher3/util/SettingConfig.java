package com.android.launcher3.util;

import android.text.TextUtils;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.Utilities;
import com.android.launcher3.bean.ConfigInfo;
import com.android.launcher3.bean.FirstOpenInfo;
import com.blankj.utilcode.util.FileIOUtils;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;

import java.util.List;

/**
 * Created by xuwei on 2021/7/14.
 */

public class SettingConfig {

    private ConfigInfo mConfig;

    public XmlPullParser getWorkspace() {
        return Utilities.getLocalXmlParser("/data/local/setting/default_workspace_5x4.xml");
    }

    public XmlPullParser getIgnoreShowApp() {
        return Utilities.getLocalXmlParser("/data/local/setting/ignore_show_apps.xml");
    }

    public XmlPullParser getIgnoreShortcutApp() {
        return Utilities.getLocalXmlParser("/data/local/setting/ignore_shortcut_app.xml");
    }

    public XmlPullParser getIgnoreUninstallApp() {
        return Utilities.getLocalXmlParser("/data/local/setting/ignore_uninstall_apps.xml");
    }

    public List<String> getUninstallBlacklist(){
        return Utilities.getLineFile("/data/local/config/UninstallBlacklist");
    }

    public FirstOpenInfo getFirstOpenInfo(String packageName) {
        String openSetting = FileIOUtils.readFile2String("/data/local/setting/firstopen.txt");
        if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(openSetting)) {
            List<FirstOpenInfo> firstOpenInfos = (List<FirstOpenInfo>) JsonUtil.parsData(openSetting, new TypeToken<List<FirstOpenInfo>>() {
            }.getType());
            if (firstOpenInfos != null && !firstOpenInfos.isEmpty()) {
                for (FirstOpenInfo openInfo : firstOpenInfos) {
                    if (openInfo.packageName != null && packageName.equals(openInfo.packageName)) {
                        return openInfo;
                    }
                }
            }
        }
        return null;
    }

    public boolean isNoRealShortCut() {
        String retString = FileIOUtils.readFile2String("/data/local/setting/nodshortcut.txt");
        return TextUtils.equals(retString, "1");
    }

    public boolean needPresetUpdate() {
        if (BuildConfig.DEBUG) {
            return true;
        }
        String retString = FileIOUtils.readFile2String("/data/local/setting/dpresetupdate.txt");
        return TextUtils.equals(retString, "1");
    }

    private ConfigInfo getConfigInfo() {
        String openSetting = FileIOUtils.readFile2String("/data/local/setting/dlauncherconfig.txt");
        if (!TextUtils.isEmpty(openSetting)) {
            mConfig = (ConfigInfo)JsonUtil.parsData(openSetting, ConfigInfo.class);
            return mConfig;
        }
        return null;
    }

    private static class SingletonHolder{
        private static final SettingConfig INSTANCE = new SettingConfig();
    }

    public static SettingConfig getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private SettingConfig() {
    }
}
