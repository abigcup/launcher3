package com.android.hwyun.installrecommend.presenter;

import android.content.Intent;
import android.util.Log;

import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.common.event.SaveAppStartDataEvent;
import com.android.hwyun.common.util.AppUtil;
import com.android.hwyun.installrecommend.contract.AssociatedAppsContract;
import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;
import com.android.hwyun.prevshortcut.event.CreateDownloadShortcutEvent;
import com.android.hwyun.statistics.StatisticsConstants;
import com.android.hwyun.statistics.StatisticsManager;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by xuwei on 2019/1/11.
 */
public class AssociateAppDialogPresenter implements AssociatedAppsContract.PopDialog.IPresenter {

    AssociatedAppsContract.PopDialog.IView view;

    public AssociateAppDialogPresenter(AssociatedAppsContract.PopDialog.IView view) {
        this.view = view;
    }

    @Override
    public void clickItem(ResponeAppsShortcut appsShortcut, int appState) {
        if (appState == CommonConstants.APP_STATE_OPEN) {
            startApp(appsShortcut);
        } else if (appState == CommonConstants.APP_STATE_INSTALL) {

            StatisticsManager.getInstance().BuriedPoint(StatisticsConstants.MarketAppDownload_AssociationApp);

            setShortCut(appsShortcut);
        }
    }

    private void downloadApp(ResponeAppsShortcut appsShortcut) {
        appsShortcut.setAutoDownload(true);
        EventBus.getDefault().post(new CreateDownloadShortcutEvent(appsShortcut));
    }

    private void setShortCut(final ResponeAppsShortcut appsShortcut) {
        appsShortcut.setFengwoName(appsShortcut.getAppName());
        if (appsShortcut.getAppType() == CommonConstants.SHORTCUT_SOURCE_FENGWO
                && AppUtil.isFengWoAvailable(appsShortcut.getAppVerId())) {
            AppUtil.setFengwoTopicShotcut(appsShortcut.getId(), appsShortcut.getFengwoName(), appsShortcut.getAppImgUrl(), new AppUtil.FengwoTopicShotcutCallBack() {
                @Override
                public void onAddShortcutSuccess(long topicID, String name) {
                    view.updateFengwoShortState(topicID);
                }
            });
        } else {
            downloadApp(appsShortcut);
        }
    }

    private void startApp(ResponeAppsShortcut appsShortcut) {
        if (appsShortcut.getAppType() == CommonConstants.SHORTCUT_SOURCE_FENGWO) {
            try {
                ActivityUtils.startActivity(AppUtil.getFengwoTopicIntent(appsShortcut.getId()));
            } catch (Exception e) {
                Log.e("start shortcut", "startApp error packageName: " + appsShortcut.getPackageName(), e);
            }
        } else {
            String packageName = appsShortcut.getPackageName();
            Intent intent = com.blankj.utilcode.util.Utils.getApp().getPackageManager().getLaunchIntentForPackage(packageName);
            AppUtil.shellLaunchApp(packageName, intent.getComponent().getClassName());
        }

        EventBus.getDefault().post(new SaveAppStartDataEvent(AppUtils.getAppName(appsShortcut.getPackageName()), appsShortcut.getPackageName()));
        view.dismissPopDialog();
    }
}
