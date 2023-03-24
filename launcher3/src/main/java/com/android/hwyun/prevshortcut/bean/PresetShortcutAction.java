package com.android.hwyun.prevshortcut.bean;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.bean.PresetShortData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suchangxu.
 * Date: 2020/8/6 18:49
 */
public class PresetShortcutAction implements Comparable<PresetShortcutAction> {

    private static final String TAG = "PresetShortcutAction";

    //仅修改数据
    public static final int ACTION_MODIFY_DATA = 0;
    //添加之前不存在的图标
    public static final int ACTION_ADD_NEW = 1;
    //删除图标
    public static final int ACTION_DELETE = 2;

    private ItemInfo mExistShortcutInfo;

    private ItemInfo mShortcutInfoFromResponse;

    private ItemInfo mDeleteItemInfo;

    private long mFolderContainer;

    private String mTargetFolderName;

    private int mAction;

    private String packageName;

    private String mShortcutName;

    //后台下发文件夹id，用来创建文件夹和图标的关系
    private long mFolderId;

    private int mSortIndex;

    public int getSortIndex() {
        return mSortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.mSortIndex = sortIndex;
    }

    public long getFolderId() {
        return mFolderId;
    }

    public void setFolderId(long folderId) {
        this.mFolderId = folderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getShortcutName() {
        return mShortcutName;
    }

    public void setShortcutName(String shortcutName) {
        this.mShortcutName = shortcutName;
    }

    public ItemInfo getExistShortcutInfo() {
        return mExistShortcutInfo;
    }

    public void setExistShortcutInfo(ItemInfo mItemInfo) {
        this.mExistShortcutInfo = mItemInfo;
    }

    public ItemInfo getShortcutInfoFromResponse() {
        return mShortcutInfoFromResponse;
    }

    public void setShortcutInfoFromResponse(ItemInfo mItemInfo) {
        this.mShortcutInfoFromResponse = mItemInfo;
    }

    public void setFolderContainer(long folderContainer) {
        mFolderContainer = folderContainer;
    }

    public void setTargetFolderName(String targetFolderName) {
        mTargetFolderName = targetFolderName;
    }

    public String getTargetFolderName() {
        return mTargetFolderName;
    }

    public long getFolderContainer() {
        return mFolderContainer;
    }

    public void setAction(int action) {
        mAction = action;
    }

    public int getAction() {
        return mAction;
    }

    public void setDeleteItemInfo(ShortcutInfo shortcutInfo) {
        mDeleteItemInfo = shortcutInfo;
    }

    public ItemInfo getDeleteItemInfo() {
        return mDeleteItemInfo;
    }

    @Override
    public String toString() {
        if (mExistShortcutInfo != null) {
            return "Exist:" + mExistShortcutInfo.title;
        } else if (mShortcutInfoFromResponse != null) {
            return "FromResponse:" + mShortcutInfoFromResponse.title + "," + mTargetFolderName;

        } else if (mExistShortcutInfo != null && mShortcutInfoFromResponse != null) {
            return "Exist:" + mExistShortcutInfo.title + "," + "FromResponse:" + mShortcutInfoFromResponse.title;
        } else {
            return "情况更复杂";
        }
    }

    @Override
    public int compareTo(@NonNull PresetShortcutAction o) {
        return mSortIndex - o.mSortIndex;
    }
}
