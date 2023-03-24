package com.android.hwyun.batchinstall.model;


import com.android.hwyun.batchinstall.bean.request.FileDownRequest;
import com.android.hwyun.batchinstall.bean.response.FileDownResponse;
import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseOkHttpHelper;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.google.gson.reflect.TypeToken;

/**
 * Date: 2022/3/24 16:07
 * Description:
 */
public class UploadModel {


    private ActivityHttpHelper httpHelperDown;
    private ActivityHttpHelper<BaseResultWrapper<FileDownResponse>> httpHelperFileDown;



    /**
     * 文件下载请求
     */
    public void requestFileDown(String fileId, String roomId, IUIDataListener iuiDataListener) {
        try {
            if (httpHelperFileDown == null) {
                TypeToken<BaseResultWrapper<FileDownResponse>> typeToken = new TypeToken<BaseResultWrapper<FileDownResponse>>() {
                };
                httpHelperFileDown = new ActivityHttpHelper<BaseResultWrapper<FileDownResponse>>(typeToken);
            }
            FileDownRequest info = new FileDownRequest();
            info.setFileId(fileId);
            info.setRoomId(roomId);
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            httpHelperFileDown.UpdateUIDataListener(iuiDataListener);
            httpHelperFileDown.sendPostRequest(new HttpConstants().FILE_DOWN, baseHttpRequest.toMapPrames(info), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件下载
     */
    public void fileDown(String url, String path, BaseOkHttpHelper.ProgressCallBack iuiDataListener) {
        try {
            if (httpHelperDown == null) {
                httpHelperDown = new ActivityHttpHelper();
            }
            httpHelperDown.downloadFile(url, path, iuiDataListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void destory() {
        if (httpHelperDown != null) {
            httpHelperDown.stopRequest();
        }
        if (httpHelperFileDown != null) {
            httpHelperFileDown.stopRequest();
        }
    }
}
