package com.android.hwyun.batchinstall.bean;

import android.text.TextUtils;

import com.obs.services.model.ObsObject;

import java.io.File;

/**
 * Created by xuwei on 2018/8/28.
 */
public class UploadedFileInfo {
    public final static String METADATA_PACKAGE_NAME = "package_name";
    public final static String METADATA_UPLOAD_TIME = "upload_time";

    public UploadedFileInfo(ObsObject obsObject) {
        this.obsObject = obsObject;
    }

    protected ObsObject obsObject;

    public ObsObject getObsObject() {
        return obsObject;
    }

    public String getFileName() {
        String filePath = obsObject.getObjectKey();
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    public long getFileSize() {
        return obsObject.getMetadata().getContentLength();
    }

    public String getPackageName() {
        return (String) obsObject.getMetadata().getUserMetadata(METADATA_PACKAGE_NAME);
    }

    public long getUploadTime() {
        String updateTime = (String) obsObject.getMetadata().getUserMetadata(METADATA_UPLOAD_TIME);
        if (!TextUtils.isEmpty(updateTime)) {
            return Long.valueOf(updateTime);
        }
        return 0;
    }

    public String getFileCloudPath() {
        return obsObject.getObjectKey();
    }

    public String getIconCloundPath() {
        return obsObject.getObjectKey();
    }
}
