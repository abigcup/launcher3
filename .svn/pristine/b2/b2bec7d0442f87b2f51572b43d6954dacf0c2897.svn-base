package com.android.hwyun.batchinstall.hwcloud;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.android.hwyun.batchinstall.bean.DownloadFileInfo;
import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.common.util.DownloadUtil;
import com.android.launcher3.R;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.obs.services.exception.ObsException;

import java.io.File;

/**
 * Created by xuwei on 2018/8/29.
 */
public class DownloadTask extends AsyncTask<String, Integer, Integer>
        implements HWYunManager.UpdateDownloadedCallBack {

    private DownloadFileInfo downloadFileInfo;
    private OnDownloadListener downloadListener;
    private int progressInterval;
    private long updateProgressTime;
    private String errorMessage;
    private ObsCert obsCert;

    private boolean isInstalling;

    public DownloadTask(ObsCert obsCert) {
        this.obsCert = obsCert;
    }

    public DownloadTask setDownloadListener(OnDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        return this;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String fileName = strings[0];
        String savePath = strings[1];
        try {
            if (!HWYunManager.getInstance().downloadObject(fileName, savePath, obsCert)) {
                return CommonConstants.DOWNLOAD_ERROR_NOT_EXIST;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ObsException) {
                if ((!TextUtils.isEmpty(((ObsException) e).getErrorCode()) && ((ObsException) e).getErrorCode().equals("NoSuchKey"))
                        || ((ObsException) e).getResponseCode() == 404) {
                    ToastUtils.showLong(R.string.http_error_404);
                    return CommonConstants.DOWNLOAD_ERROR_NOT_EXIST;
                }
            }
            return CommonConstants.DOWNLOAD_ERROR_FILE;
        }
        publishProgress(CommonConstants.DOWNLOAD_SUCCESS);
        isInstalling = true;
        String installResult = DownloadUtil.installAppSilent(new File(downloadFileInfo.getSavePath()), "-r", true);
        isInstalling = false;
        if (!DownloadUtil.isInstallSuccess(installResult)) {
            errorMessage = installResult;
            return CommonConstants.DOWNLOAD_INSTALL_ERROR;
        }
        return CommonConstants.DOWNLOAD_INSTALL_SUCCESS;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (downloadListener != null) {
            switch (values[0]) {
                case CommonConstants.DOWNLOAD_DOWNLOADING:
                    downloadListener.progress(downloadFileInfo);
                    break;
                case CommonConstants.DOWNLOAD_SUCCESS:
                    downloadFileInfo.setDownloaded(true);
                    downloadListener.downloadCompleted(downloadFileInfo);
                    break;
            }
        }
    }

    @Override
    protected void onPostExecute(Integer aInteger) {
        if (downloadListener != null) {
            if (aInteger == CommonConstants.DOWNLOAD_INSTALL_SUCCESS) {
                downloadListener.installCompleted(downloadFileInfo);
            } else {
                LogUtils.eTag("BatchInstallLog", String.format("task error code : %d", aInteger));
                if (aInteger == CommonConstants.DOWNLOAD_ERROR_NOT_EXIST) {
                    errorMessage = "文件不存在";
                } else if (aInteger == CommonConstants.DOWNLOAD_ERROR_FILE) {
                    errorMessage = "下载失败";
                }
                downloadListener.error(downloadFileInfo, aInteger, errorMessage);
            }
        }
    }

    @Override
    public void onUpdateDownloaded(long contentSize, long downloadedSize, long downloadSpeed) {
        downloadFileInfo.setContentSize(contentSize);
        downloadFileInfo.setFileSoFarBytes(downloadedSize);
        downloadFileInfo.setSpeedBytes(downloadSpeed);
        long updateTime = System.currentTimeMillis();
        if (updateTime - updateProgressTime > progressInterval) {
            publishProgress(CommonConstants.DOWNLOAD_DOWNLOADING);
            updateProgressTime = updateTime;
        }
    }

    public DownloadFileInfo getDownloadFileInfo() {
        return downloadFileInfo;
    }

    public DownloadTask setDownloadFileInfo(DownloadFileInfo downloadFileInfo) {
        this.downloadFileInfo = downloadFileInfo;
        return this;
    }

    public DownloadTask setProgressInterval(int progressInterval) {
        this.progressInterval = progressInterval;
        return this;
    }
}
