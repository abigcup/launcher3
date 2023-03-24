package com.android.hwyun.batchinstall.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuwei on 2018/8/29.
 */
public class DownloadFileInfo {

    protected String obsObjectKey;
    protected String downloadID;
    protected long contentSize;
    protected long fileSoFarBytes;
    protected long speedBytes;
    protected String savePath;
    protected boolean isDownloaded;
    protected Map<String, Object> mapTag = new HashMap<>();

    public DownloadFileInfo(String obsObjectKey, String downloadID, String savePath) {
        this.obsObjectKey = obsObjectKey;
        this.downloadID = downloadID;
        this.savePath = savePath;
    }

    public String getObsObjectKey() {
        return obsObjectKey;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

    public long getFileSoFarBytes() {
        return fileSoFarBytes;
    }

    public void setFileSoFarBytes(long fileSoFarBytes) {
        this.fileSoFarBytes = fileSoFarBytes;
    }

    public long getSpeedBytes() {
        return speedBytes;
    }

    public void setSpeedBytes(long speedBytes) {
        this.speedBytes = speedBytes;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(String downloadID) {
        this.downloadID = downloadID;
    }

    public int getProgress() {
        return (int)(fileSoFarBytes*100 / contentSize);
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public Object getTag(String key) {
        return mapTag.get(key);
    }

    public void setTag(String key, Object value) {
        mapTag.put(key, value);
    }
}
