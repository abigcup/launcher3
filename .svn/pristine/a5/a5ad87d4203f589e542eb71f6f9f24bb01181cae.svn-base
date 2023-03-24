package com.android.hwyun.preapp.presenter;

import android.text.TextUtils;
import android.view.View;

import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.CLog;
import com.android.hwyun.common.util.UserInfoUtil;
import com.android.hwyun.preapp.Contract.PreAppContract;
import com.android.hwyun.preapp.bean.PresetAppInfo;
import com.android.hwyun.preapp.model.PreAppModel;
import com.android.hwyun.prevshortcut.presenter.PrevAppsShortcutPresent;

public
/**
 * Created by xuwei on 2021/7/14.
 */

class PreAppPresenter {

    private PreAppModel model;

    public PreAppPresenter() {
        model = new PreAppModel();
    }

    public void checkPresetUpdate(final String packageName, final int verCode, final String appName, final PreAppContract.IView iView, final View v) {
        model.getPresetApp(UserInfoUtil.getOrderID(), new IUIDataListener() {
            @Override
            public void uiDataSuccess(Object object) {
                BaseResultWrapper<PresetAppInfo> wrapper = (BaseResultWrapper<PresetAppInfo>) object;
                if (wrapper != null && wrapper.code == 1 && wrapper.data != null && wrapper.data.InstallApps != null) {
                    PresetAppInfo.InstallAppsBean ret = null;
                    for (PresetAppInfo.InstallAppsBean appsBean : wrapper.data.InstallApps) {
                        if (TextUtils.equals(appsBean.AppPackageName, packageName)
                                && appsBean.VersionCode > verCode
                                && !TextUtils.isEmpty(appsBean.AppUrl)) {
                            ret = appsBean;
                            ret.AppName = appName;
                            break;
                        }
                    }
                    CLog.i(PrevAppsShortcutPresent.class.getSimpleName(), "getPresetApp ret is " + ret == null ? "null":"ok");
                    iView.presetAppUpdateResult(ret, v);
                } else {
                    CLog.i(PrevAppsShortcutPresent.class.getSimpleName(), "getPresetApp null: " + wrapper==null?"":String.valueOf(wrapper.code));
                    iView.presetAppUpdateResult(null, v);
                }
            }

            @Override
            public void uiDataError(Exception error) {
                CLog.i(PrevAppsShortcutPresent.class.getSimpleName(), "getPresetApp error: " + error.getMessage());
                iView.presetAppUpdateResult(null, v);
            }
        });
    }
}
