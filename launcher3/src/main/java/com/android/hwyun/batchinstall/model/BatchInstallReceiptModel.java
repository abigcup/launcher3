package com.android.hwyun.batchinstall.model;

import com.android.hwyun.batchinstall.bean.request.RequestBatchInstallReceipt;
import com.android.hwyun.batchinstall.bean.response.ResponseBatchInstallReceipt;
import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xuwei on 2018/10/16.
 */
public class BatchInstallReceiptModel {
    /**
     * 回执通知安装任务的执行结果
     */
    private ActivityHttpHelper<BaseResultWrapper<ResponseBatchInstallReceipt>> httpHelper;

    /**
     * 回执通知安装任务的执行结果
     */
    public void requestAppBlackList(RequestBatchInstallReceipt request, IUIDataListener iuiDataListener) {
        try {
            if (httpHelper == null) {
                TypeToken<BaseResultWrapper<ResponseBatchInstallReceipt>> typeToken = new TypeToken<BaseResultWrapper<ResponseBatchInstallReceipt>>() {
                };
                httpHelper = new ActivityHttpHelper<>(iuiDataListener, typeToken);
            }
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            httpHelper.sendPostRequest(HttpConstants.BATCH_INSTALL_RECEIPT, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destory(){
        if (httpHelper != null) {
            httpHelper.stopRequest();
        }
    }
}
