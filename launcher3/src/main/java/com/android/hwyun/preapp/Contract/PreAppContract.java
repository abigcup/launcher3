package com.android.hwyun.preapp.Contract;

import android.view.View;

import com.android.hwyun.preapp.bean.PresetAppInfo;

public
/**
 * Created by xuwei on 2021/7/14.
 */

interface PreAppContract {
    interface IView {
        //通用sdk预置应用是否更新，只有咪咕用
        void presetAppUpdateResult(PresetAppInfo.InstallAppsBean appsBean, View v);
    }
}
