package com.android.hwyun.appstart.model;

import com.android.hwyun.appstart.bean.request.RequestSaveAppStartUpData;
import com.android.hwyun.appstart.bean.response.ResponseSaveAppStartUpData;
import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.PhoneIDUtil;
import com.android.hwyun.common.util.UserInfoUtil;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xuwei on 2018/12/15.
 */
public class SaveAppStartUpDataModel {
    /**
     * 获取应用黑名单
     */
    private ActivityHttpHelper<BaseResultWrapper<ResponseSaveAppStartUpData>> httpHelper;

    /**
     * 获取应用黑名单
     */
    public void requestSaveAppStartUpData(IUIDataListener iuiDataListener, String appName, String packageName) {
        try {
            if (httpHelper == null) {
                TypeToken<BaseResultWrapper<ResponseSaveAppStartUpData>> typeToken = new TypeToken<BaseResultWrapper<ResponseSaveAppStartUpData>>() {
                };
                httpHelper = new ActivityHttpHelper<>(iuiDataListener, typeToken);
            }
            RequestSaveAppStartUpData request = new RequestSaveAppStartUpData();
            request.AppName = appName;
            request.AppPackageName = packageName;
            request.PhoneId = PhoneIDUtil.getInstance().getPhoneID();
            request.OrderId = UserInfoUtil.getOrderID();
            request.AppSource = CommonConstants.APP_SOURCE_LAUNCHER;
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            httpHelper.sendPostRequest(HttpConstants.APPMARKET_SAVE_APPSTARTUPDATA, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
