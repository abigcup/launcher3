package com.android.hwyun.preapp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2021/2/23 15:18
 * Description:
 */
public class PresetAppInfo implements Parcelable {

    public List<InstallAppsBean> InstallApps;
    public List<UnInstallAppsBean> UnInstallApps;
    /**
     * DesktopRefreshObsInfo : {"ObsKey":"sample string 1","RemoteFilePath":"sample string 2","EndPoint":"sample string 3","BucketName":"sample string 4","AK":"sample string 5","SK":"sample string 6"}
     */

    public DesktopRefreshObsInfoBean DesktopRefreshObsInfo;

    public static class InstallAppsBean implements Parcelable {
        /**
         * AppID : 1
         * AppName : sample string 2
         * AppPackageName : sample string 3
         * AppUrl : sample string 4
         */

        public int AppID;
        public String AppName;
        public String AppPackageName;
        public String AppUrl;
        public String AppVer;
        public int VersionCode;
        public int UpdateType;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.AppID);
            dest.writeString(this.AppName);
            dest.writeString(this.AppPackageName);
            dest.writeString(this.AppUrl);
            dest.writeString(this.AppVer);
            dest.writeInt(this.VersionCode);
            dest.writeInt(this.UpdateType);
        }

        public void readFromParcel(Parcel source) {
            this.AppID = source.readInt();
            this.AppName = source.readString();
            this.AppPackageName = source.readString();
            this.AppUrl = source.readString();
            this.AppVer = source.readString();
            this.VersionCode = source.readInt();
            this.UpdateType = source.readInt();
        }

        public InstallAppsBean() {
        }

        protected InstallAppsBean(Parcel in) {
            this.AppID = in.readInt();
            this.AppName = in.readString();
            this.AppPackageName = in.readString();
            this.AppUrl = in.readString();
            this.AppVer = in.readString();
            this.VersionCode = in.readInt();
            this.UpdateType = in.readInt();
        }

        public static final Creator<InstallAppsBean> CREATOR = new Creator<InstallAppsBean>() {
            @Override
            public InstallAppsBean createFromParcel(Parcel source) {
                return new InstallAppsBean(source);
            }

            @Override
            public InstallAppsBean[] newArray(int size) {
                return new InstallAppsBean[size];
            }
        };
    }

    public static class UnInstallAppsBean implements Parcelable {
        /**
         * AppPackageName : sample string 1
         */

        public String AppPackageName;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.AppPackageName);
        }

        public void readFromParcel(Parcel source) {
            this.AppPackageName = source.readString();
        }

        public UnInstallAppsBean() {
        }

        protected UnInstallAppsBean(Parcel in) {
            this.AppPackageName = in.readString();
        }

        public static final Creator<UnInstallAppsBean> CREATOR = new Creator<UnInstallAppsBean>() {
            @Override
            public UnInstallAppsBean createFromParcel(Parcel source) {
                return new UnInstallAppsBean(source);
            }

            @Override
            public UnInstallAppsBean[] newArray(int size) {
                return new UnInstallAppsBean[size];
            }
        };
    }

    public static class DesktopRefreshObsInfoBean implements Parcelable {
        /**
         * ObsKey : sample string 1
         * RemoteFilePath : sample string 2
         * EndPoint : sample string 3
         * BucketName : sample string 4
         * AK : sample string 5
         * SK : sample string 6
         */

        public String ObsKey;
        public String RemoteFilePath;
        public String EndPoint;
        public String BucketName;
        public String AK;
        public String SK;
        public int RefreshMode;
        public String RefreshCommand;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.ObsKey);
            dest.writeString(this.RemoteFilePath);
            dest.writeString(this.EndPoint);
            dest.writeString(this.BucketName);
            dest.writeString(this.AK);
            dest.writeString(this.SK);
            dest.writeInt(this.RefreshMode);
            dest.writeString(this.RefreshCommand);
        }

        public void readFromParcel(Parcel source) {
            this.ObsKey = source.readString();
            this.RemoteFilePath = source.readString();
            this.EndPoint = source.readString();
            this.BucketName = source.readString();
            this.AK = source.readString();
            this.SK = source.readString();
            this.RefreshMode = source.readInt();
            this.RefreshCommand = source.readString();
        }

        public DesktopRefreshObsInfoBean() {
        }

        protected DesktopRefreshObsInfoBean(Parcel in) {
            this.ObsKey = in.readString();
            this.RemoteFilePath = in.readString();
            this.EndPoint = in.readString();
            this.BucketName = in.readString();
            this.AK = in.readString();
            this.SK = in.readString();
            this.RefreshMode = in.readInt();
            this.RefreshCommand = in.readString();
        }

        public static final Creator<DesktopRefreshObsInfoBean> CREATOR = new Creator<DesktopRefreshObsInfoBean>() {
            @Override
            public DesktopRefreshObsInfoBean createFromParcel(Parcel source) {
                return new DesktopRefreshObsInfoBean(source);
            }

            @Override
            public DesktopRefreshObsInfoBean[] newArray(int size) {
                return new DesktopRefreshObsInfoBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.InstallApps);
        dest.writeList(this.UnInstallApps);
        dest.writeParcelable(this.DesktopRefreshObsInfo, flags);
    }

    public void readFromParcel(Parcel source) {
        this.InstallApps = new ArrayList<InstallAppsBean>();
        source.readList(this.InstallApps, InstallAppsBean.class.getClassLoader());
        this.UnInstallApps = new ArrayList<UnInstallAppsBean>();
        source.readList(this.UnInstallApps, UnInstallAppsBean.class.getClassLoader());
        this.DesktopRefreshObsInfo = source.readParcelable(DesktopRefreshObsInfoBean.class.getClassLoader());
    }

    public PresetAppInfo() {
    }

    protected PresetAppInfo(Parcel in) {
        this.InstallApps = new ArrayList<InstallAppsBean>();
        in.readList(this.InstallApps, InstallAppsBean.class.getClassLoader());
        this.UnInstallApps = new ArrayList<UnInstallAppsBean>();
        in.readList(this.UnInstallApps, UnInstallAppsBean.class.getClassLoader());
        this.DesktopRefreshObsInfo = in.readParcelable(DesktopRefreshObsInfoBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<PresetAppInfo> CREATOR = new Parcelable.Creator<PresetAppInfo>() {
        @Override
        public PresetAppInfo createFromParcel(Parcel source) {
            return new PresetAppInfo(source);
        }

        @Override
        public PresetAppInfo[] newArray(int size) {
            return new PresetAppInfo[size];
        }
    };
}
