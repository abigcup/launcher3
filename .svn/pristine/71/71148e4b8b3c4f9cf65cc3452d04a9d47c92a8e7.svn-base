package com.android.hwyun.common.net;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


/**
 *
 * Created by linbinghuang on 2016/4/12.
 */
public class BaseResultWrapper<T> implements Parcelable {

    @SerializedName(value = "msg",alternate = {"Msg"})
    public String msg;
    @SerializedName(value = "code",alternate = {"Code"})
    public Integer code;
    @SerializedName(value = "sign",alternate = {"Sign"})
    public String sign;
    @SerializedName(value = "r",alternate = {"R"})
    public int r;
    @SerializedName(value = "data",alternate = {"Data"})
    public T data;
    @SerializedName(value = "msgtype",alternate = {"Msgtype"})
    public int msgtype;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
        dest.writeValue(this.code);
        dest.writeString(this.sign);
        dest.writeInt(this.r);
        Bundle bundle = new Bundle();
        bundle.putParcelable(NomalConstans.DATA_KEY, (Parcelable) data);
        dest.writeBundle(bundle);
        dest.writeInt(this.msgtype);
    }

    public BaseResultWrapper() {
    }

    protected BaseResultWrapper(Parcel in) {
        this.msg = in.readString();
        this.code = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sign = in.readString();
        this.r = in.readInt();
        this.data = in.readBundle().getParcelable(NomalConstans.DATA_KEY);
        this.msgtype = in.readInt();
    }

    public static final Creator<BaseResultWrapper> CREATOR = new Creator<BaseResultWrapper>() {
        @Override
        public BaseResultWrapper createFromParcel(Parcel source) {
            return new BaseResultWrapper(source);
        }

        @Override
        public BaseResultWrapper[] newArray(int size) {
            return new BaseResultWrapper[size];
        }
    };
}
