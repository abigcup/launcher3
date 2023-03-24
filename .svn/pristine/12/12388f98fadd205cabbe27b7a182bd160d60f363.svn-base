package com.android.hwyun.ddyobs.model;

import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.ddyobs.bean.request.CreatCertRequestInfo;
import com.android.hwyun.ddyobs.bean.request.DeviceOrderRequest;
import com.android.hwyun.ddyobs.bean.response.CreatCertResponse;
import com.android.hwyun.ddyobs.bean.response.DeviceOrderResponse;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xuwei on 2020/2/19.
 */
public class ObsRequestModel {
    /**
     * obs访问参数
     */
    private ActivityHttpHelper mObsCreatCertHttpHelper;

    /**
     * obs访问参数
     *
     * @param opType
     * @param ucid
     * @param dataListener
     */
    public void requestObsCreatCert(int opType, int deviceRegion, String fileName, String ucid, IUIDataListener dataListener) {
        try {
            if (mObsCreatCertHttpHelper == null) {
                TypeToken<BaseResultWrapper<CreatCertResponse>> typeToken = new TypeToken<BaseResultWrapper<CreatCertResponse>>() {
                };
                mObsCreatCertHttpHelper = new ActivityHttpHelper<>(typeToken);
            }

            CreatCertRequestInfo info = new CreatCertRequestInfo();
            info.UCID = ucid;
            info.OpType = opType;
            info.DeviceRegion = deviceRegion;
            info.FileName = fileName;
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            mObsCreatCertHttpHelper.UpdateUIDataListener(dataListener);
            mObsCreatCertHttpHelper.sendPostRequest(HttpConstants.obs_creatcert, baseHttpRequest.toMapPrames(info), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设备订单信息查询
     */
    private ActivityHttpHelper mApiDeviceOrder;

    /**
     * 设备订单信息查询
     *
     * @param deviceCodes
     * @param ucid
     * @param dataListener
     */
    public void requestApiDeviceOrder(String deviceCodes, String ucid, IUIDataListener dataListener) {
        try {
            if (mApiDeviceOrder == null) {
                TypeToken<BaseResultWrapper<List<DeviceOrderResponse>>> typeToken = new TypeToken<BaseResultWrapper<List<DeviceOrderResponse>>>() {
                };
                mApiDeviceOrder = new ActivityHttpHelper<>(typeToken);
            }

            DeviceOrderRequest info = new DeviceOrderRequest();
            info.UCID = ucid;
            info.DeviceCodes = deviceCodes;
            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            mApiDeviceOrder.UpdateUIDataListener(dataListener);
            mApiDeviceOrder.sendPostRequest(HttpConstants.api_deviceorder, baseHttpRequest.toMapPrames(info), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
