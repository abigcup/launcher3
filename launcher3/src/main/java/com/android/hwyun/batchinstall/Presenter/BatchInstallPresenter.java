package com.android.hwyun.batchinstall.Presenter;

import android.os.Environment;
import android.text.TextUtils;

import com.android.hwyun.batchinstall.bean.DownloadFileInfo;
import com.android.hwyun.batchinstall.bean.ExtraBean;
import com.android.hwyun.batchinstall.bean.request.InstallApkCommandRequest;
import com.android.hwyun.batchinstall.bean.request.RequestBatchInstallReceipt;
import com.android.hwyun.batchinstall.bean.response.FileDownResponse;
import com.android.hwyun.batchinstall.bean.response.ResponseBatchInstallReceipt;
import com.android.hwyun.batchinstall.contract.BatchInstallContract;
import com.android.hwyun.batchinstall.hwcloud.BatchInstallManager;
import com.android.hwyun.batchinstall.hwcloud.OnDownloadListener;
import com.android.hwyun.batchinstall.model.BatchInstallReceiptModel;
import com.android.hwyun.batchinstall.model.UploadModel;
import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.common.net.BaseOkHttpHelper;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.DownloadUtil;
import com.android.hwyun.common.util.ObsUtils;
import com.android.hwyun.common.util.PhoneIDUtil;
import com.android.hwyun.ddyobs.ObsContract;
import com.android.hwyun.ddyobs.ObsRequestHelper;
import com.android.hwyun.ddyobs.bean.response.CreatCertResponse;
import com.android.hwyun.ddyobs.bean.response.DeviceOrderResponse;
import com.android.hwyun.ddyobs.constans.ObsConstans;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ddy.httplib.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * Created by xuwei on 2018/10/11.
 */
public class BatchInstallPresenter implements BatchInstallContract.IPresenter, OnDownloadListener {

    private Map<String, List<ExtraBean>> mapInstalltaskInfos;
    private BatchInstallReceiptModel model;
    private UploadModel dModel;

    public BatchInstallPresenter() {
        mapInstalltaskInfos = new HashMap<>();
        BatchInstallManager.getImpl().setResultListener(this);
        model = new BatchInstallReceiptModel();
        dModel = new UploadModel();
//        installTest();
    }

    @Override
    public void downloadAndInstall(final ExtraBean installTaskInfo) {
        LogUtils.iTag("down", "startDown" + installTaskInfo.getTaskID());
        if (StringUtils.isEmpty(installTaskInfo.getFileId())) {
            //兼容原obs方式
            final String savePath = DownloadUtil.getDefaultSavePath(installTaskInfo.getOBKey());
            final long id = DownloadUtil.generateId(installTaskInfo.getOBKey(), savePath);
            List<ExtraBean> installTaskInfos = mapInstalltaskInfos.get(id + "");
            if (installTaskInfos == null || installTaskInfos.isEmpty()) {
                installTaskInfos = new ArrayList<>();
                installTaskInfos.add(installTaskInfo);
                mapInstalltaskInfos.put(id + "", installTaskInfos);

                ObsRequestHelper.getInstance().getThisDeviceOrder(PhoneIDUtil.getInstance().getPhoneID(), installTaskInfo.getUCID(), new ObsContract.Callback<DeviceOrderResponse>() {
                    @Override
                    public void onSuccess(DeviceOrderResponse data) {
                        int deviceRegion = (data == null ? 0 : data.DeviceRegion);
                        int opType = ObsConstans.OPTYPE_DOWNFILE;
                        if (installTaskInfo.getBucketType() == CommonConstants.BUCKET_TYPE_APPS) {
                            opType = ObsConstans.OPTYPE_DOWNFILE_APP;
                        }
                        ObsRequestHelper.getInstance().requestObsCreatCert(opType, deviceRegion, installTaskInfo.getOBKey(), installTaskInfo.getUCID(), new ObsContract.Callback<CreatCertResponse>() {
                            @Override
                            public void onSuccess(CreatCertResponse data) {
                                BatchInstallManager.getImpl().downloadInstall(installTaskInfo.getOBKey(), savePath, ObsUtils.FromCertResponse(data));
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                error(new DownloadFileInfo(installTaskInfo.getOBKey(), id + "", savePath), CommonConstants.DOWNLOAD_CERT_ERROR, "请求空间失败");
                            }
                        });

                    }

                    @Override
                    public void onFail(int code, String msg) {
                        error(new DownloadFileInfo(installTaskInfo.getOBKey(), id + "", savePath), CommonConstants.DOWNLOAD_CERT_ERROR, "请求空间失败");
                    }
                });
            } else {
                installTaskInfos.add(installTaskInfo);
            }
        } else {
            //新的http模式下载
            final String savePath = Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppPackageName() + File.separator + EncryptUtils.encryptMD5ToString(installTaskInfo.getFileId()) + ".apk";
            //创建文件夹
            FileUtils.createOrExistsDir(Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppPackageName() + File.separator);
            if (mapInstalltaskInfos.containsKey(installTaskInfo.getFileId())) {
                //任务存在
                LogUtils.iTag("down", "have" + installTaskInfo.getTaskID());
                mapInstalltaskInfos.get(installTaskInfo.getFileId()).add(installTaskInfo);
                return;
            } else {
                List<ExtraBean> installTaskInfos = new ArrayList<>();
                installTaskInfos.add(installTaskInfo);
                mapInstalltaskInfos.put(installTaskInfo.getFileId(), installTaskInfos);
                ObsRequestHelper.getInstance().getThisDeviceOrder(PhoneIDUtil.getInstance().getPhoneID(), installTaskInfo.getUCID(), new ObsContract.Callback<DeviceOrderResponse>() {
                    @Override
                    public void onSuccess(DeviceOrderResponse data) {
                        int deviceRegion = (data == null ? 0 : data.DeviceRegion);
                        dModel.requestFileDown(installTaskInfo.getFileId(), deviceRegion + "", new IUIDataListener() {
                            @Override
                            public void uiDataSuccess(Object object) {
                                BaseResultWrapper<FileDownResponse> wrapper = (BaseResultWrapper<FileDownResponse>) object;
                                if (wrapper == null || wrapper.code != 1) {
                                    LogUtils.iTag("down", "downFileFail" + wrapper.msg);
                                    error(new DownloadFileInfo("", installTaskInfo.getFileId(), savePath), CommonConstants.DOWNLOAD_CERT_ERROR, "请求下载链接失败");
                                } else {
                                    LogUtils.iTag("down", "downFile" + wrapper.data.getDownUrl());
                                    if (!StringUtils.isEmpty(wrapper.data.getDownUrl())) {
                                        dModel.fileDown(wrapper.data.getDownUrl(), savePath, new BaseOkHttpHelper.ProgressCallBack() {
                                            @Override
                                            public void onProgress(Disposable disposable, long total, long current) {
                                                //LogUtils.iTag("down", "down" + current + "/" + total);
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                LogUtils.iTag("down", "downFail" + e.toString());
                                                error(new DownloadFileInfo("", installTaskInfo.getFileId(), savePath), CommonConstants.DOWNLOAD_CERT_ERROR, "下载失败");
                                            }

                                            @Override
                                            public void onResponse() {
                                                LogUtils.iTag("down", "downSuccess|" + savePath + "|" + installTaskInfo.getFileId());
                                                String installResult = DownloadUtil.installAppSilent(new File(savePath), "-r", true);
                                                if (!DownloadUtil.isInstallSuccess(installResult)) {
                                                    error(new DownloadFileInfo("", installTaskInfo.getFileId(), savePath), CommonConstants.DOWNLOAD_CERT_ERROR, "安装失败");
                                                } else {
                                                    installCompleted(new DownloadFileInfo("", installTaskInfo.getFileId(), savePath));
                                                }

                                            }
                                        });
                                    } else {
                                        error(new DownloadFileInfo("", installTaskInfo.getFileId(), savePath), CommonConstants.DOWNLOAD_CERT_ERROR, "请求下载链接失败");
                                    }

                                }

                            }

                            @Override
                            public void uiDataError(Exception error) {
                                error(new DownloadFileInfo("", installTaskInfo.getFileId(), savePath), CommonConstants.DOWNLOAD_CERT_ERROR, "请求下载链接失败");
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        error(new DownloadFileInfo("", installTaskInfo.getFileId(), savePath), CommonConstants.DOWNLOAD_CERT_ERROR, "请求空间失败");
                    }
                });
            }

        }

    }

    @Override
    public void downloadAndInstall(List<ExtraBean> installTaskInfo) {
        for (ExtraBean extraBean : installTaskInfo) {
            downloadAndInstall(extraBean);
        }
    }

    @Override
    public void commitResult(final String downloadID, int result, final String errorMessage) {
        List<ExtraBean> installTaskInfos = mapInstalltaskInfos.remove(downloadID);
        if (installTaskInfos != null) {
            for (final ExtraBean info : installTaskInfos) {
                RequestBatchInstallReceipt requestBatchInstallReceipt = new RequestBatchInstallReceipt();
                requestBatchInstallReceipt.UCID = info.getUCID();
                requestBatchInstallReceipt.OrderID = info.getOrderID();
                requestBatchInstallReceipt.TaskID = info.getTaskID();
                requestBatchInstallReceipt.InstallStatus = result;
                requestBatchInstallReceipt.InstallRemark = errorMessage;
                model.requestAppBlackList(requestBatchInstallReceipt, new IUIDataListener() {
                    @Override
                    public void uiDataSuccess(Object object) {
                        BaseResultWrapper<ResponseBatchInstallReceipt> wrapper = (BaseResultWrapper<ResponseBatchInstallReceipt>) object;
                        if (wrapper == null || wrapper.code != 1) {
                            LogUtils.eTag("BatchInstallLog",
                                    String.format("安装结果提交失败 code:%d, msg;%s", wrapper.code, wrapper.msg));
                        } else {
                            LogUtils.iTag("BatchInstallLog", "安装结果提交成功" + downloadID + "|" + info.getTaskID() + "|" + errorMessage);
                        }
                    }

                    @Override
                    public void uiDataError(Exception error) {
                        LogUtils.eTag("BatchInstallLog",
                                String.format("安装结果提交失败 Exception:%s", error.toString()));
                    }
                });
            }
        }
    }

    @Override
    public void progress(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void downloadCompleted(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void installCompleted(DownloadFileInfo downloadFileInfo) {
        FileUtils.deleteFile(downloadFileInfo.getSavePath());
        commitResult(downloadFileInfo.getDownloadID(), CommonConstants.STATE_INSTALL_SUCCESS, "");
    }

    @Override
    public void error(DownloadFileInfo downloadFileInfo, int errorCode, String errorMessage) {
        FileUtils.deleteFile(downloadFileInfo.getSavePath());
        commitResult(downloadFileInfo.getDownloadID(), CommonConstants.STATE_INSTALL_FAIL, errorMessage);
    }

    private void installTest() {
        LogUtils.dTag("BatchInstallLog", "installTest on");
        String jsonMsg = "{\"extra\":{\"OBKey\":\"D36417A152128021861D2314B3F8131E/apk/test.apk\",\"UCID\":\"1FBC2C7C305CEEAD\",\"TaskID\":\"111\",\"OrderID\":10126777},\"command\":\"BroadHW\",\"time\":1539936885083}";
        InstallApkCommandRequest commandRequest = (InstallApkCommandRequest) JsonUtil.parsData(jsonMsg, InstallApkCommandRequest.class);
        if (commandRequest != null && commandRequest.getExtra() != null) {
            downloadAndInstall(commandRequest.getExtra());
        }
    }
}
