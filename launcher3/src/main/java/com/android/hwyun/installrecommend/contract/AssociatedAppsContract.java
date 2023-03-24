package com.android.hwyun.installrecommend.contract;

import com.android.hwyun.installrecommend.bean.response.AssociatedAppsResponse;
import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;

/**
 * Created by xuwei on 2019/1/8.
 */
public interface AssociatedAppsContract {
    interface IView {
        void showAssociatedView(AssociatedAppsResponse response);
    }

    interface IPresenter {
        void getAssociatedApps(long channelID, String installedName);
    }

    interface PopDialog {
        interface IView {
            void updateFengwoShortState(long TopicID);
            void dismissPopDialog();
        }

        interface IPresenter {
            void clickItem(ResponeAppsShortcut appsShortcut, int appState);
        }
    }
}
