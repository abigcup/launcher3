package com.android.hwyun.prevshortcut.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by lijingying on 2019/1/8.
 * <p>
 * http://app.ddyun123.com/Help/Api/POST-AppMarket-AppsShortcut
 */
public class ResponeAppsShortcut implements Parcelable, Cloneable{

    /**
     * Id : 1
     * AppName : sample string 2
     * AppImgUrl : sample string 3
     * AppType : 1
     * Channeld : 4
     * ChannelName : sample string 5
     * MD5 : sample string 6
     * AppVerId : 7
     * PackageName : sample string 8
     * AttachPackageName : sample string 9
     * ApkObsUrl : sample string 10
     * ApkPath : sample string 11
     * AppSource : 12
     */

    private long Id;
    private String AppName;
    private String AppImgUrl;
    private int AppType;
    private long Channeld;
    private String ChannelName;
    private String MD5;
    private int AppVerId;
    private String PackageName;
    private String AttachPackageName;
    private String ApkObsUrl;
    private String ApkPath;
    private int AppSource;

    private int installedpercent; //本地数据，假的推荐百分比
    private boolean autoDownload; //本地数据，创建图标后是否自动开始下载
    private String fengwoName; //本地数据，辅助显示名称
    private int SortIndex;//图标排序

    public int getSortIndex() {
        return SortIndex;
    }

    public void setSortIndex(int sortIndex) {
        SortIndex = sortIndex;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String AppName) {
        this.AppName = AppName;
    }

    public String getAppImgUrl() {
        return AppImgUrl;
    }

    public void setAppImgUrl(String AppImgUrl) {
        this.AppImgUrl = AppImgUrl;
    }

    public int getAppType() {
        return AppType;
    }

    public void setAppType(int AppType) {
        this.AppType = AppType;
    }

    public long getChanneld() {
        return Channeld;
    }

    public void setChanneld(long Channeld) {
        this.Channeld = Channeld;
    }

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String ChannelName) {
        this.ChannelName = ChannelName;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public int getAppVerId() {
        return AppVerId;
    }

    public void setAppVerId(int AppVerId) {
        this.AppVerId = AppVerId;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String PackageName) {
        this.PackageName = PackageName;
    }

    public String getAttachPackageName() {
        return AttachPackageName;
    }

    public void setAttachPackageName(String AttachPackageName) {
        this.AttachPackageName = AttachPackageName;
    }

    public String getApkObsUrl() {
        return ApkObsUrl;
    }

    public void setApkObsUrl(String ApkObsUrl) {
        this.ApkObsUrl = ApkObsUrl;
    }

    public String getApkPath() {
        return ApkPath;
    }

    public void setApkPath(String ApkPath) {
        this.ApkPath = ApkPath;
    }

    public int getAppSource() {
        return AppSource;
    }

    public void setAppSource(int AppSource) {
        this.AppSource = AppSource;
    }

    public int getInstalledpercent() {
        return installedpercent;
    }

    public void setInstalledpercent(int installedpercent) {
        this.installedpercent = installedpercent;
    }

    public boolean isAutoDownload() {
        return autoDownload;
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload = autoDownload;
    }

    public String getFengwoName() {
        if (!TextUtils.isEmpty(fengwoName)) {
            return fengwoName;
        }
        return AppName;
    }

    public void setFengwoName(String fengwoName) {
        this.fengwoName = fengwoName;
    }

    public ResponeAppsShortcut() {
    }

    public ResponeAppsShortcut clone() {
        ResponeAppsShortcut clone = null;
        try {
            clone = (ResponeAppsShortcut) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.Id);
        dest.writeString(this.AppName);
        dest.writeString(this.AppImgUrl);
        dest.writeInt(this.AppType);
        dest.writeLong(this.Channeld);
        dest.writeString(this.ChannelName);
        dest.writeString(this.MD5);
        dest.writeInt(this.AppVerId);
        dest.writeString(this.PackageName);
        dest.writeString(this.AttachPackageName);
        dest.writeString(this.ApkObsUrl);
        dest.writeString(this.ApkPath);
        dest.writeInt(this.AppSource);
        dest.writeInt(this.installedpercent);
        dest.writeByte(this.autoDownload ? (byte) 1 : (byte) 0);
        dest.writeString(this.fengwoName);
        dest.writeInt(this.SortIndex);
    }

    protected ResponeAppsShortcut(Parcel in) {
        this.Id = in.readLong();
        this.AppName = in.readString();
        this.AppImgUrl = in.readString();
        this.AppType = in.readInt();
        this.Channeld = in.readLong();
        this.ChannelName = in.readString();
        this.MD5 = in.readString();
        this.AppVerId = in.readInt();
        this.PackageName = in.readString();
        this.AttachPackageName = in.readString();
        this.ApkObsUrl = in.readString();
        this.ApkPath = in.readString();
        this.AppSource = in.readInt();
        this.installedpercent = in.readInt();
        this.autoDownload = in.readByte() != 0;
        this.fengwoName = in.readString();
        this.SortIndex = in.readInt();
    }

    public static final Creator<ResponeAppsShortcut> CREATOR = new Creator<ResponeAppsShortcut>() {
        @Override
        public ResponeAppsShortcut createFromParcel(Parcel source) {
            return new ResponeAppsShortcut(source);
        }

        @Override
        public ResponeAppsShortcut[] newArray(int size) {
            return new ResponeAppsShortcut[size];
        }
    };

    public boolean isAppInfoAvailable() {
        return !(TextUtils.isEmpty(ApkObsUrl) || TextUtils.isEmpty(AppImgUrl) || TextUtils.isEmpty(PackageName));
    }

    @Override
    public String toString() {
        return getAppName() + "," + getSortIndex();
    }
}
