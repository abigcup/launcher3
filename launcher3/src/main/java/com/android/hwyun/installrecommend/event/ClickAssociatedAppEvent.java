package com.android.hwyun.installrecommend.event;

import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;

/**
 * Created by xuwei on 2019/1/11.
 */
public class ClickAssociatedAppEvent {

    private ResponeAppsShortcut responeAppsShortcut;
    private int appState;

    public ClickAssociatedAppEvent(ResponeAppsShortcut responeAppsShortcut, int appState) {
        this.responeAppsShortcut = responeAppsShortcut;
        this.appState = appState;
    }

    public ResponeAppsShortcut getResponeAppsShortcut() {
        return responeAppsShortcut;
    }

    public void setResponeAppsShortcut(ResponeAppsShortcut responeAppsShortcut) {
        this.responeAppsShortcut = responeAppsShortcut;
    }

    public int getAppState() {
        return appState;
    }

    public void setAppState(int appState) {
        this.appState = appState;
    }
}
