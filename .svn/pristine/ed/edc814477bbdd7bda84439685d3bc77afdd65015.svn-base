package com.android.hwyun.prevshortcut.presenter;

import android.text.TextUtils;

import com.android.hwyun.preapp.bean.CheckUpdateInfo;
import com.android.launcher3.BuildConfig;
import com.android.launcher3.util.JsonUtil;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public
/**
 * Created by xuwei on 2021/7/20.
 */

class PreUpdateManager {
    public static final String PRE_APP_UPDATE_INFO = "pre_app_update_info";

    private HashMap<String, CheckUpdateInfo> map;

    public boolean isChecked(String packageName, int checkVerCode) {
        if (BuildConfig.DEBUG) {
            return false;
        }
        CheckUpdateInfo info = map.get(packageName);
        if (info != null) {
            return TextUtils.equals(info.checkDate, getCurrentDate());
        }
        return false;
    }

    public void setCheckUpdateInfo(String packageName, int checkVerCode) {
        CheckUpdateInfo info = new CheckUpdateInfo();
        info.packageName = packageName;
        info.checkVerCode = checkVerCode;
        info.checkDate = getCurrentDate();
        map.put(info.packageName, info);
        SPUtils.getInstance().put(PRE_APP_UPDATE_INFO, JsonUtil.objectToString(map));
    }

    public void removeCheckUpdateInfo(String packageName) {
        if (map.containsKey(packageName)) {
            map.remove(packageName);
            SPUtils.getInstance().put(PRE_APP_UPDATE_INFO, JsonUtil.objectToString(map));
        }
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
    }

    private PreUpdateManager() {
        if (map == null) {
            String strMap = SPUtils.getInstance().getString(PRE_APP_UPDATE_INFO);
            if (!TextUtils.isEmpty(strMap)) {
                map = (HashMap<String, CheckUpdateInfo>)JsonUtil.parsData(strMap, new TypeToken<HashMap<String, CheckUpdateInfo>>(){}.getType());
            }
        }
        if (map == null) {
            map = new HashMap<>();
        }
    }

    private static class SingletonHolder{
        private static final PreUpdateManager INSTANCE = new PreUpdateManager();
    }
    //获取单例
    public static PreUpdateManager getInstance(){
        return PreUpdateManager.SingletonHolder.INSTANCE;
    }
}
