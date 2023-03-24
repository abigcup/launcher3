package com.android.hwyun.installrecommend.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuwei on 2019/1/8.
 */
public class AssociatedAppsResponse implements Parcelable {

    private String installedName;   //被关联的应用，本地数据

    private List<ResponeAppsShortcut> AppsShortcutList;
    /**
     * AssociatedDesc : sample string 1
     */

    private String AssociatedDesc;

    public String getInstalledName() {
        return installedName;
    }

    public void setInstalledName(String installedName) {
        this.installedName = installedName;
    }

    public List<ResponeAppsShortcut> getAppsShortcutList() {
        return AppsShortcutList;
    }

    public void setAppsShortcutList(List<ResponeAppsShortcut> AppList) {
        this.AppsShortcutList = AppList;
    }

    public String getAssociatedDesc() {
        return AssociatedDesc;
    }

    public void setAssociatedDesc(String AssociatedDesc) {
        this.AssociatedDesc = AssociatedDesc;
    }

    public AssociatedAppsResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.installedName);
        dest.writeList(this.AppsShortcutList);
        dest.writeString(this.AssociatedDesc);
    }

    protected AssociatedAppsResponse(Parcel in) {
        this.installedName = in.readString();
        this.AppsShortcutList = new ArrayList<ResponeAppsShortcut>();
        in.readList(this.AppsShortcutList, ResponeAppsShortcut.class.getClassLoader());
        this.AssociatedDesc = in.readString();
    }

    public static final Creator<AssociatedAppsResponse> CREATOR = new Creator<AssociatedAppsResponse>() {
        @Override
        public AssociatedAppsResponse createFromParcel(Parcel source) {
            return new AssociatedAppsResponse(source);
        }

        @Override
        public AssociatedAppsResponse[] newArray(int size) {
            return new AssociatedAppsResponse[size];
        }
    };
}
