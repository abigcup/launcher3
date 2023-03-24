package com.android.hwyun.batchinstall.bean;

import com.android.launcher3.ItemInfo;
import com.android.launcher3.ShortcutInfo;

/**
 * Created by suchangxu.
 * Date: 2020/8/5 13:30
 */
public class ShortDownloadFileInfo extends DownloadFileInfo {

    private ShortcutInfo shortcutInfo;

    public ShortDownloadFileInfo(ShortcutInfo shortcutInfo, String obsObjectKey, long downloadID, String savePath) {
        super(obsObjectKey, downloadID+"", savePath);
        this.shortcutInfo = shortcutInfo;
    }

    public ShortcutInfo getShortcutInfo() {
        return shortcutInfo;
    }

    public void setShortcutInfo(ShortcutInfo shortcutInfo) {
        this.shortcutInfo = shortcutInfo;
    }
}
