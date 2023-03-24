package com.android.hwyun.common.event;

/**
 * Created by xuwei on 2019/1/14.
 */
public class SaveAppStartDataEvent {

    private String appName;
    private String packageName;

    public SaveAppStartDataEvent(String appName, String packageName) {
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
