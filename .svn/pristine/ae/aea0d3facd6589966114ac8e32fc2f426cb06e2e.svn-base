package com.android.hwyun.installrecommend.model;

import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.UserInfoUtil;
import com.android.hwyun.installrecommend.bean.request.AssociatedAppsRequest;
import com.android.hwyun.installrecommend.bean.response.AssociatedAppsResponse;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xuwei on 2019/1/8.
 */
public class AssociatedAppsModel {
    /**
     * 关联应用
     */
    private ActivityHttpHelper<BaseResultWrapper<AssociatedAppsResponse>> httpHelper;

    /**
     * 关联应用
     */
    public void requestAssociatedApps(IUIDataListener iuiDataListener, long channelID) {
        try {
            if (httpHelper == null) {
                TypeToken<BaseResultWrapper<AssociatedAppsResponse>> typeToken = new TypeToken<BaseResultWrapper<AssociatedAppsResponse>>() {
                };
                httpHelper = new ActivityHttpHelper<>(iuiDataListener, typeToken);
            }
            AssociatedAppsRequest request = new AssociatedAppsRequest();
            request.ChannelId = channelID;
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            httpHelper.sendPostRequest(HttpConstants.APPMARKET_ASSOCIATED_APPS_SHORTCUT, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destory(){
        if (httpHelper != null) {
            httpHelper.stopRequest();
        }
    }
}
