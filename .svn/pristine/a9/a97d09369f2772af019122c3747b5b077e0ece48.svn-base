package com.android.hwyun.batchinstall.contract;

import com.android.hwyun.batchinstall.bean.ExtraBean;

import java.util.List;

/**
 * Created by xuwei on 2018/10/11.
 */
public interface BatchInstallContract {
    interface IView {
    }

    interface IPresenter {
        void downloadAndInstall(ExtraBean installTaskInfo);
        void downloadAndInstall(List<ExtraBean> installTaskInfo);
        void commitResult(String downloadID, int result, String errorMessage);
    }
}
