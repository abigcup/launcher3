package com.android.hwyun.prevshortcut.presenter;

import android.util.Log;

import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.UserInfoUtil;
import com.android.hwyun.common.util.Utils;
import com.android.hwyun.installrecommend.bean.response.RecommendAppsResponse;
import com.android.hwyun.installrecommend.event.ShowRecommendMsgEvent;
import com.android.hwyun.prevshortcut.PrevShortcutContract;
import com.android.hwyun.prevshortcut.bean.RequestAppsShortcut;
import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;
import com.android.hwyun.common.bean.XBYUserInfo;
import com.android.hwyun.prevshortcut.model.PrevAppsShortcutModel;
import com.android.launcher3.BuildConfig;
import com.blankj.utilcode.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

/**
 * Created by lijingying on 2019/1/9.
 */
public class PrevAppsShortcutPresent implements PrevShortcutContract.InitPresent {
    //下载快捷方式
    private PrevShortcutContract.DetailPresent prevShortcutPresenter;

    @Override
    public void process(List<String> appPackageList, final boolean bRecommendMsg, final XBYUserInfo userInfo) {

        RequestAppsShortcut requestAppsShortcut = new RequestAppsShortcut();
        String list = null;
        for (String a : appPackageList) {
            list += a;
            list += ",";
        }
        requestAppsShortcut.setPackageNames(list);
        if (userInfo != null) {
            requestAppsShortcut.UCID = userInfo.UCID;
            requestAppsShortcut.ChannelName = userInfo.Channel;
            requestAppsShortcut.DDYAppVersionCode = userInfo.ddyVerCode;
            requestAppsShortcut.setOrderID(userInfo.OrderId);
        }
        if (requestAppsShortcut.getOrderID() == 0) {
            requestAppsShortcut.setOrderID(UserInfoUtil.getOrderID());
        }

        new PrevAppsShortcutModel().requestAppsShortcut(requestAppsShortcut, new IUIDataListener() {
            @Override
            public void uiDataSuccess(Object object) {
                BaseResultWrapper<List<ResponeAppsShortcut>> wrapper = (BaseResultWrapper<List<ResponeAppsShortcut>>) object;
                if (wrapper == null || wrapper.code != 1) {
                    uiDataError(new Exception("ResponeAppsShortcut wrapper null"));
                } else {

                    String nams = "";
                    for (ResponeAppsShortcut shortcut : wrapper.data) {
                        nams += shortcut.getAppName();
                        nams += " , ";
                    }

                    Log.i("shortcut", "ResponeAppsShortcut size=" + wrapper.data.size() + " :" + nams);

                    //如果这里清除不及时，需要考虑其它位置(需求是每次进来时，都要更新)
                    prevShortcutPresenter.create(wrapper.data);
                    //获取推荐弹窗信息
                    //IOS V1.5.2多文件夹兼容此处单图标时，产品说禁用该功能
                    //禁用
//                    if (bRecommendMsg) {
//                        getRecommendApps(wrapper.data, userInfo);
//                    }
                }
            }

            @Override
            public void uiDataError(Exception error) {
                LogUtils.eTag("shortcut", "ResponeAppsShortcut uiDataError " + error.getMessage());

                if (BuildConfig.DEBUG) {
                    //如果这里清除不及时，需要考虑其它位置(需求是每次进来时，都要更新)
                    prevShortcutPresenter.create(PrevAppsShortcutModel.getTestData());
                }
            }
        });

    }

    @Override
    public void init(PrevShortcutContract.DetailPresent p) {
        prevShortcutPresenter = p;
    }

    private void getRecommendApps(List<ResponeAppsShortcut> shortcutApps, XBYUserInfo userInfo) {
        String channelIDs = "";
        if (Utils.getListSize(shortcutApps) != 0) {
            channelIDs = String.valueOf(shortcutApps.get(0).getChanneld());
            for (int i = 1; i < shortcutApps.size(); ++i) {
                channelIDs += ("," + String.valueOf(shortcutApps.get(i).getChanneld()));
            }
        }
        new PrevAppsShortcutModel().requestRecommendApps(channelIDs, userInfo, new IUIDataListener() {
            @Override
            public void uiDataSuccess(Object object) {
                BaseResultWrapper<RecommendAppsResponse> wrapper = (BaseResultWrapper<RecommendAppsResponse>) object;
                if (wrapper == null || wrapper.code != 1) {
                    LogUtils.eTag(PrevAppsShortcutPresent.class.getSimpleName(), "getRecommendApps Error");
                } else {
                    Log.i(PrevAppsShortcutPresent.class.getSimpleName(), "getRecommendApps Success");
                    if (wrapper.data != null && Utils.getListSize(wrapper.data.getAppsAssociateList()) != 0) {
                        Iterator<ResponeAppsShortcut> it = wrapper.data.getAppsAssociateList().iterator();
                        while (it.hasNext()) {
                            ResponeAppsShortcut shortcut = it.next();
                            if (!shortcut.isAppInfoAvailable()) {
                                it.remove();
                            }
                        }
                        if (Utils.getListSize(wrapper.data.getAppsAssociateList()) != 0) {
                            EventBus.getDefault().post(new ShowRecommendMsgEvent(wrapper.data));
                        }
                    }
                }
            }

            @Override
            public void uiDataError(Exception error) {
                LogUtils.eTag(PrevAppsShortcutPresent.class.getSimpleName(), "getRecommendApps Error");
            }
        });
    }
}
