package com.android.hwyun.installrecommend.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;

import java.util.List;

/**
 * Created by xuwei on 2019/1/18.
 */
public class RecommendAppsResponse implements Parcelable {


    /**
     * AssociatedDesc : sample string 1
     * ToastImg : sample string 2
     * ToastTitle : sample string 3
     * ShowSeconds : sample string 4
     */

    private String AssociatedDesc;
    private String ToastImg;
    private String ToastTitle;
    private int ShowSeconds;

    private List<ResponeAppsShortcut> AppsAssociateList;

    public String getAssociatedDesc() {
        return AssociatedDesc;
    }

    public void setAssociatedDesc(String AssociatedDesc) {
        this.AssociatedDesc = AssociatedDesc;
    }

    public String getToastImg() {
        return ToastImg;
    }

    public void setToastImg(String ToastImg) {
        this.ToastImg = ToastImg;
    }

    public String getToastTitle() {
        return ToastTitle;
    }

    public void setToastTitle(String ToastTitle) {
        this.ToastTitle = ToastTitle;
    }

    public int getShowSeconds() {
        return ShowSeconds;
    }

    public void setShowSeconds(int ShowSeconds) {
        this.ShowSeconds = ShowSeconds;
    }

    public List<ResponeAppsShortcut> getAppsAssociateList() {
        return AppsAssociateList;
    }

    public void setAppsAssociateList(List<ResponeAppsShortcut> appsShortcutList) {
        AppsAssociateList = appsShortcutList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AssociatedDesc);
        dest.writeString(this.ToastImg);
        dest.writeString(this.ToastTitle);
        dest.writeInt(this.ShowSeconds);
        dest.writeTypedList(this.AppsAssociateList);
    }

    public RecommendAppsResponse() {
    }

    protected RecommendAppsResponse(Parcel in) {
        this.AssociatedDesc = in.readString();
        this.ToastImg = in.readString();
        this.ToastTitle = in.readString();
        this.ShowSeconds = in.readInt();
        this.AppsAssociateList = in.createTypedArrayList(ResponeAppsShortcut.CREATOR);
    }

    public static final Creator<RecommendAppsResponse> CREATOR = new Creator<RecommendAppsResponse>() {
        @Override
        public RecommendAppsResponse createFromParcel(Parcel source) {
            return new RecommendAppsResponse(source);
        }

        @Override
        public RecommendAppsResponse[] newArray(int size) {
            return new RecommendAppsResponse[size];
        }
    };
}
