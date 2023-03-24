package com.android.hwyun.batchinstall.hwcloud;

import com.android.hwyun.batchinstall.bean.DownloadFileInfo;

/**
 * Created by xuwei on 2018/8/29.
 */
public interface OnDownloadListener {
    void progress(DownloadFileInfo downloadFileInfo);
    void downloadCompleted(DownloadFileInfo downloadFileInfo);
    void installCompleted(DownloadFileInfo downloadFileInfo);
    void error(DownloadFileInfo downloadFileInfo, int errorCode, String errorMessage);
}
