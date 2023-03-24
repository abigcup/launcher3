package com.android.hwyun.prevshortcut.event;

import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;

/**
 * Created by xuwei on 2019/1/11.
 */
public class CreateDownloadShortcutEvent {

    private ResponeAppsShortcut responeAppsShortcut;

    public CreateDownloadShortcutEvent(ResponeAppsShortcut responeAppsShortcut) {
        this.responeAppsShortcut = responeAppsShortcut;
    }

    public ResponeAppsShortcut getResponeAppsShortcut() {
        return responeAppsShortcut;
    }

    public void setResponeAppsShortcut(ResponeAppsShortcut responeAppsShortcut) {
        this.responeAppsShortcut = responeAppsShortcut;
    }
}
