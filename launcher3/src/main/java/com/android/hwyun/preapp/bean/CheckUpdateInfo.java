package com.android.hwyun.preapp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public
/**
 * Created by xuwei on 2021/7/20.
 */

class CheckUpdateInfo implements Parcelable, Serializable {
    public int checkVerCode;
    public String checkDate;
    public String packageName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.checkVerCode);
        dest.writeString(this.checkDate);
        dest.writeString(this.packageName);
    }

    public void readFromParcel(Parcel source) {
        this.checkVerCode = source.readInt();
        this.checkDate = source.readString();
        this.packageName = source.readString();
    }

    public CheckUpdateInfo() {
    }

    protected CheckUpdateInfo(Parcel in) {
        this.checkVerCode = in.readInt();
        this.checkDate = in.readString();
        this.packageName = in.readString();
    }

    public static final Parcelable.Creator<CheckUpdateInfo> CREATOR = new Parcelable.Creator<CheckUpdateInfo>() {
        @Override
        public CheckUpdateInfo createFromParcel(Parcel source) {
            return new CheckUpdateInfo(source);
        }

        @Override
        public CheckUpdateInfo[] newArray(int size) {
            return new CheckUpdateInfo[size];
        }
    };
}
