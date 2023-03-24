package com.android.hwyun.preapp.model;

import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.UserInfoUtil;
import com.android.hwyun.preapp.bean.PresetAppInfo;
import com.android.hwyun.preapp.bean.PresetAppRequest;
import com.google.gson.reflect.TypeToken;

public
/**
 * Created by xuwei on 2021/7/14.
 */

class PreAppModel {

    private ActivityHttpHelper presetHelper;

    public void getPresetApp(long OrderId, IUIDataListener iuiDataListener) {
        try {
            if (presetHelper == null) {
                TypeToken<BaseResultWrapper<PresetAppInfo>> typeToken = new TypeToken<BaseResultWrapper<PresetAppInfo>>() {
                };
                presetHelper = new ActivityHttpHelper<>(typeToken);
            }
            PresetAppRequest info = new PresetAppRequest();
            info.OrderId = OrderId;
            //需要对应appid
            info.AppId = UserInfoUtil.getSdkAppKey();
            presetHelper.UpdateUIDataListener(iuiDataListener);
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            presetHelper.sendPostRequest(HttpConstants.APP_PRESET_APP, baseHttpRequest.toMapPrames(info), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (presetHelper != null) {
            presetHelper.stopRequest();
        }
    }
}
