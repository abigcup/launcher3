package com.android.hwyun.batchinstall.hwcloud;

import android.os.AsyncTask;

import com.android.hwyun.batchinstall.bean.DownloadFileInfo;
import com.android.hwyun.common.util.DownloadUtil;

/**
 * Created by xuwei on 2018/10/9.
 */
public class BatchInstallManager {

    private OnDownloadListener resultListener;
    private OnBatchInstallListenerBase downloadListenerBase;
    private int progressInterval = 500; //进度通知时间间隔毫秒

    private final static class HolderClass {
        private final static BatchInstallManager INSTANCE
                = new BatchInstallManager();
    }

    public static BatchInstallManager getImpl() {
        return HolderClass.INSTANCE;
    }

    public BatchInstallManager() {
        downloadListenerBase = new OnBatchInstallListenerBase();
    }

    public void downloadInstall(String objectKey, String savePath, ObsCert obsCert) {
        int id = DownloadUtil.generateId(objectKey, savePath);
        DownloadTask task = new DownloadTask(obsCert)
                .setDownloadFileInfo(new DownloadFileInfo(objectKey, id+"", savePath))
                .setDownloadListener(downloadListenerBase)
                .setProgressInterval(progressInterval);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objectKey, savePath);
    }

    public class OnBatchInstallListenerBase implements OnDownloadListener {
        @Override
        public void progress(DownloadFileInfo downloadFileInfo) {
        }

        @Override
        public void downloadCompleted(DownloadFileInfo downloadFileInfo) {
        }

        @Override
        public void installCompleted(DownloadFileInfo downloadFileInfo) {
            if (resultListener != null) {
                resultListener.installCompleted(downloadFileInfo);
            }
        }

        @Override
        public void error(DownloadFileInfo downloadFileInfo, int errorCode, String errorMessage) {
            if (resultListener != null) {
                resultListener.error(downloadFileInfo, errorCode, errorMessage);
            }
        }
    }

    public void setResultListener(OnDownloadListener resultListener) {
        this.resultListener = resultListener;
    }
}
