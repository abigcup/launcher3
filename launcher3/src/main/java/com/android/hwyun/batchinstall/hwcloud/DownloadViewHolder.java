package com.android.hwyun.batchinstall.hwcloud;

import android.view.View;

import com.android.hwyun.batchinstall.bean.DownloadFileInfo;

/**
 * Created by xuwei on 2018/8/30.
 */
public interface DownloadViewHolder {

    String getDownloadID();
    void setDownloadID(String downloadID);

    void updateProgress(DownloadFileInfo fileInfo);
    void downloadError(int errorCode);
    void downloadSuccess(DownloadFileInfo fileInfo);
    void installSuccess(DownloadFileInfo fileInfo);
}
