package com.android.hwyun.prevshortcut.model;

import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.installrecommend.bean.request.RecommendAppsRequest;
import com.android.hwyun.installrecommend.bean.response.RecommendAppsResponse;
import com.android.hwyun.prevshortcut.bean.RequestAppsShortcut;
import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;
import com.android.hwyun.common.bean.XBYUserInfo;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijy on 2019/1/9.
 *
 *  http://app.ddyun123.com/Help/Api/POST-AppMarket-AppsShortcut
 */
public class PrevAppsShortcutModel {
    /**
     * 快捷方式应用
     */
    private ActivityHttpHelper<BaseResultWrapper<List<ResponeAppsShortcut>>> httpHelper;

    /**
     * Toast应用列表和图片对象
     */
    private ActivityHttpHelper<BaseResultWrapper<RecommendAppsResponse>> recommendAppttpHelper;

    /**
     * 快捷方式应用
     */
    public void requestAppsShortcut(RequestAppsShortcut request, IUIDataListener iuiDataListener) {
        try {
            if (httpHelper == null) {
                TypeToken<BaseResultWrapper<List<ResponeAppsShortcut>>> typeToken = new TypeToken<BaseResultWrapper<List<ResponeAppsShortcut>>>() {
                };
                httpHelper = new ActivityHttpHelper<>(iuiDataListener, typeToken);
            }
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            httpHelper.sendPostRequest(HttpConstants.APPMARKET_APPS_SHORTCUT, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            LogUtils.eTag("shortcut",e.getMessage());
        }
    }

    /**
     * Toast应用列表和图片对象
     */
    public void requestRecommendApps(String ChannelIds, XBYUserInfo userInfo, IUIDataListener iuiDataListener) {
        try {
            if (recommendAppttpHelper == null) {
                TypeToken<BaseResultWrapper<RecommendAppsResponse>> typeToken = new TypeToken<BaseResultWrapper<RecommendAppsResponse>>() {
                };
                recommendAppttpHelper = new ActivityHttpHelper<>(iuiDataListener, typeToken);
            }
            RecommendAppsRequest request = new RecommendAppsRequest();
            request.ChannelIds = ChannelIds;
            if (userInfo != null) {
                request.UCID = userInfo.UCID;
                request.ChannelName = userInfo.Channel;
                request.DDYAppVersionCode = userInfo.ddyVerCode;
            }
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            recommendAppttpHelper.sendPostRequest(HttpConstants.APPMARKET_APPS_TOASTAPPS, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            LogUtils.eTag("shortcut",e.getMessage());
        }
    }

    public void destory(){
        if (httpHelper != null) {
            httpHelper.stopRequest();
        }
        if (recommendAppttpHelper != null) {
            recommendAppttpHelper.stopRequest();
        }
    }

    public static List<ResponeAppsShortcut> getTestData(){
        LogUtils.eTag("shortcut", "ResponeAppsShortcut getTestData");

        List<ResponeAppsShortcut> list =  new ArrayList<>();
        ResponeAppsShortcut appsShortcut;

//        appsShortcut = new ResponeAppsShortcut();
//        appsShortcut.setApkObsUrl("App_Data/Game_App/com.tencent.tmgp.cf/base.apk");
//        appsShortcut.setAppImgUrl("http://res.ddyun123.com/Img/2018/10/30/9bdb3586ff0f408bbf798e1add67a3d0.jpg");
//        appsShortcut.setId(20069);
//        appsShortcut.setAppName("穿越火线");
//        appsShortcut.setAppType(1);
//        appsShortcut.setChanneld(20107);
//        appsShortcut.setChannelName(" 腾讯");
//        appsShortcut.setPackageName("com.tencent.tmgp.cf");
//        list.add(appsShortcut);

        appsShortcut = new ResponeAppsShortcut();
        appsShortcut.setApkObsUrl("App_Data/Game_App/com.eg.android.AlipayGphone/base.apk");
        appsShortcut.setAppImgUrl("http://res.ddyun123.com/Img/2019/01/10/2784d901f7644a35a0226bc83eeb086e.jpg");
        appsShortcut.setId(20070);
        appsShortcut.setAppName("支付宝");
        appsShortcut.setAppType(1);
        appsShortcut.setChanneld(20110);
        appsShortcut.setChannelName(" 122");
        appsShortcut.setPackageName("com.eg.android.AlipayGphone");
        list.add(appsShortcut);

        return list;
    }
}
