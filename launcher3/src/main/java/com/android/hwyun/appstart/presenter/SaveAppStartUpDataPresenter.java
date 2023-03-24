package com.android.hwyun.appstart.presenter;

import android.util.Log;

import com.android.hwyun.appstart.bean.response.ResponseSaveAppStartUpData;
import com.android.hwyun.appstart.model.SaveAppStartUpDataModel;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.blankj.utilcode.util.LogUtils;

/**
 * Created by xuwei on 2018/12/17.
 */
public class SaveAppStartUpDataPresenter {

    private SaveAppStartUpDataModel model;

    public SaveAppStartUpDataPresenter() {
        model = new SaveAppStartUpDataModel();
    }

    public void saveAppStartUpData(String appName, String packageName) {
        model.requestSaveAppStartUpData(new IUIDataListener() {
            @Override
            public void uiDataSuccess(Object object) {
                BaseResultWrapper<ResponseSaveAppStartUpData> wrapper = (BaseResultWrapper<ResponseSaveAppStartUpData>) object;
                if (wrapper == null || wrapper.code != 1) {
                    LogUtils.eTag(SaveAppStartUpDataPresenter.class.getSimpleName(), "saveAppStartUpData Error");
                } else {
                    Log.i(SaveAppStartUpDataPresenter.class.getSimpleName(), "saveAppStartUpData Success");
                }
            }

            @Override
            public void uiDataError(Exception error) {
                LogUtils.eTag(SaveAppStartUpDataPresenter.class.getSimpleName(), "saveAppStartUpData Error");
            }
        }, appName, packageName);
    }
}
