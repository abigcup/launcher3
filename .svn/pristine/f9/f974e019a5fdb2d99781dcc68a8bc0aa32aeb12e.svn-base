package com.android.hwyun.batchinstall.hwcloud;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.android.hwyun.batchinstall.bean.UploadedFileInfo;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.DownloadFileRequest;
import com.obs.services.model.DownloadFileResult;
import com.obs.services.model.HeaderResponse;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HWYunManager {

    //访问域名 访问域名是桶在互联网中的域名地址，可应用于直接通过域名访问桶的场景，比如：云应用开发、数据分享等。
//    public static final String URL = "http://down.ifengwoo.com.obs.cn-east-2.myhwclouds.com";

    public interface UpdateDownloadedCallBack {
        void onUpdateDownloaded(final long contentSize,final long downloadedSize,final long downloadSpeed);
    }

    /**
     * 关闭
     */
    public void closeObsClient(ObsClient obsClient) {
        if (obsClient != null) {
            try {
                obsClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void uploadObject(String fileName, String filePath, ResultCallback resultCallback, ObsCert obsCert) {
        long start = SystemClock.currentThreadTimeMillis();
        ObsClient obsClient = new ObsClient(obsCert.ak, obsCert.sk, obsCert.securityToken, obsCert.endPoint);
        if (obsClient != null) {
            PutObjectResult putObjectResult = obsClient.putObject(obsCert.bucketName, fileName, new File(filePath));
            long end = SystemClock.currentThreadTimeMillis();
            String msg = "uploadObject " + fileName + " time: " + (end - start);
            Log.e("HWYunManager", msg);
            if (resultCallback != null) {
                resultCallback.onResult(putObjectResult);
            }
            closeObsClient(obsClient);
        }
    }

    public boolean downloadObject(String fileName, String savePath, UpdateDownloadedCallBack callBack, ObsCert obsCert) throws IOException {
        long start = SystemClock.currentThreadTimeMillis();
        long start1 = System.currentTimeMillis();
        ObsClient obsClient = new ObsClient(obsCert.ak, obsCert.sk, obsCert.securityToken, obsCert.endPoint);
        ObsObject obsObject = obsClient.getObject(obsCert.bucketName, fileName);
        if (obsObject == null) {
            closeObsClient(obsClient);
            return false;
        }
        InputStream content = obsObject.getObjectContent();
        Long contentLength = obsObject.getMetadata().getContentLength();
        if (content != null) {
            FileUtils.createOrExistsDir(FileUtils.getDirName(savePath));
            File tempFile = new File(savePath + ".tmp");
            FileUtils.deleteFile(tempFile);
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            long currentSize = 0;
            byte[] buffer = new byte[20480];
            int length = 0;
            while ((length = content.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
                currentSize += length;
                long takeTime = System.currentTimeMillis() - start1;
                long speedByte = 0;
                if (takeTime > 0) {
                    speedByte = currentSize*1000 / takeTime;
                }
                Log.i("GetObject", "\n" + currentSize);
                callBack.onUpdateDownloaded(contentLength, currentSize, speedByte);
            }

            fileOutputStream.close();
            content.close();
            File saveFile = new File(savePath);
            FileUtils.deleteFile(saveFile);
            tempFile.renameTo(saveFile);
        }
        closeObsClient(obsClient);
        long end = SystemClock.currentThreadTimeMillis();
        long end1 = System.currentTimeMillis();
        String msg = "downloadObject " + fileName + " time: " + (end - start) + "【" + (end1 - start1) + "】size: " + contentLength;
        Log.e("HWYunManager", msg);
        return true;
    }

    public boolean downloadObject(String fileName, String savePath, ObsCert obsCert) {
        long start = SystemClock.currentThreadTimeMillis();
        long start1 = System.currentTimeMillis();
        FileUtils.deleteFile(savePath+".tmp");
        FileUtils.createOrExistsDir(FileUtils.getDirName(savePath));
        ObsClient obsClient = new ObsClient(obsCert.ak, obsCert.sk, obsCert.securityToken, obsCert.endPoint);
        ObsObject obsObject = obsClient.getObject(obsCert.bucketName, fileName);
        if (obsObject == null) {
            LogUtils.eTag("BatchInstallLog", "BatchInstallLog : " + fileName + " null");
            closeObsClient(obsClient);
            return false;
        }
        DownloadFileRequest request = new DownloadFileRequest(obsCert.bucketName, fileName);
        // 设置下载对象的本地文件路径
        request.setDownloadFile(savePath);
        // 设置分段下载时的最大并发数
        request.setTaskNum(1);
        // 设置分段大小为10MB
        request.setPartSize(10 * 1024 * 1024);
        // 开启断点续传模式
        request.setEnableCheckpoint(true);
        //失败重试10次
        int retry = 0;
        boolean downloadOver = false;
        LogUtils.iTag("BatchInstallLog", "BatchInstallLog : downloadObject " + fileName ,true);
        do {
            try {
                // 进行断点续传下载
                DownloadFileResult result = obsClient.downloadFile(request);
                downloadOver = true;
                LogUtils.eTag("BatchInstallLog", "BatchInstallLog : " + fileName + " DownloadFileResult :" + (result == null ? "" : result.toString()), true);
            } catch (ObsException e) {
                e.printStackTrace();
                // 发生异常时可再次调用断点续传下载接口进行重新下载
                ++retry;
                LogUtils.eTag("BatchInstallLog", "BatchInstallLog : " + " retry: " + retry + " download .err " + e.toString(), true);
                try {
                    Thread.sleep(1000);
                } catch (Exception le) {
                }
            }
        } while (!downloadOver && retry < 10);
        closeObsClient(obsClient);
        FileUtils.deleteFile(savePath+".tmp");
        if (!downloadOver) {
            FileUtils.deleteFile(savePath);
        }
        long end = SystemClock.currentThreadTimeMillis();
        long end1 = System.currentTimeMillis();
        String msg = "downloadObject " + fileName + " isSuccess: " + downloadOver + " time: " + (end - start) + "【" + (end1 - start1) + "】";
        LogUtils.iTag("BatchInstallLog", "BatchInstallLog : " + msg, true);
        return downloadOver;
    }

    public interface ResultCallback{
        void onResult(PutObjectResult putObjectResult);
    }


    private HWYunManager() {
    }

    private static HWYunManager manager;

    public static HWYunManager getInstance() {
        HWYunManager sManager = manager;
        if (manager == null) {
            synchronized (HWYunManager.class) {
                sManager = manager;
                if (sManager == null) {
                    sManager = new HWYunManager();
                    manager = sManager;
                }
            }

        }
        return sManager;
    }
}
