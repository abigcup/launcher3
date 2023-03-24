package com.android.hwyun.installrecommend.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.Utils;
import com.android.hwyun.installrecommend.bean.response.AssociatedAppsResponse;
import com.android.hwyun.installrecommend.contract.AssociatedAppsContract;
import com.android.hwyun.installrecommend.model.AssociatedAppsModel;
import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;
import com.android.launcher3.LauncherModel;
import com.blankj.utilcode.util.LogUtils;

import java.util.Iterator;

/**
 * Created by xuwei on 2019/1/8.
 */
public class AssociatedAppsPresenter implements AssociatedAppsContract.IPresenter {

    private AssociatedAppsContract.IView iView;
    private AssociatedAppsModel model;
    private String installAppName;

    public AssociatedAppsPresenter(AssociatedAppsContract.IView iView) {
        model = new AssociatedAppsModel();
        this.iView = iView;
    }

    @Override
    public void getAssociatedApps(long channelID, String installedName) {
        installAppName = installedName;
        model.requestAssociatedApps(new IUIDataListener() {
            @Override
            public void uiDataSuccess(Object object) {
                BaseResultWrapper<AssociatedAppsResponse> wrapper = (BaseResultWrapper<AssociatedAppsResponse>) object;
                if (wrapper == null || wrapper.code != 1) {
                    LogUtils.eTag(AssociatedAppsPresenter.class.getSimpleName(), "getAssociatedApps Error");
                } else {
                    Log.i(AssociatedAppsPresenter.class.getSimpleName(), "getAssociatedApps Success");
                    if (wrapper.data != null && Utils.getListSize(wrapper.data.getAppsShortcutList()) != 0) {
                        //不显示已有的蜂窝脚本快捷方式
                        Iterator<ResponeAppsShortcut> it = wrapper.data.getAppsShortcutList().iterator();
                        while(it.hasNext()){
                            ResponeAppsShortcut shortcut = it.next();
                            if((shortcut.getAppType() == CommonConstants.SHORTCUT_SOURCE_FENGWO
                                    && LauncherModel.isExistShortcut(shortcut.getAppName()))
                                    || !shortcut.isAppInfoAvailable()){
                                it.remove();
                            }
                        }
                        if (Utils.getListSize(wrapper.data.getAppsShortcutList()) != 0) {
                            wrapper.data.setInstalledName(installAppName);
                            iView.showAssociatedView(wrapper.data);
                        }
                    }
                }
            }

            @Override
            public void uiDataError(Exception error) {
                LogUtils.eTag(AssociatedAppsPresenter.class.getSimpleName(), "getAssociatedApps Error");
            }
        }, channelID);
    }
}
