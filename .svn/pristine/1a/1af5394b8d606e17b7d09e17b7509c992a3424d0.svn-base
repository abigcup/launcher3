package com.android.hwyun.ddyobs;

import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.CLog;
import com.android.hwyun.ddyobs.bean.response.CreatCertResponse;
import com.android.hwyun.ddyobs.bean.response.DeviceOrderResponse;
import com.android.hwyun.ddyobs.model.ObsRequestModel;

import java.util.List;

/**
 * Created by xuwei on 2020/2/19.
 */
public class ObsRequestHelper {

    //当前设备的设备订单信息
    private DeviceOrderResponse deviceOrder;

    private ObsRequestHelper() {
    }

    private static class LazyHolder {
        private static final ObsRequestHelper INSTANCE = new ObsRequestHelper();
    }

    public static ObsRequestHelper getInstance() {
        return ObsRequestHelper.LazyHolder.INSTANCE;
    }


    /**
     * obs访问参数
     * @param opType
     * @param deviceRegion
     * @param fileName
     * @param ucid
     * @param busDataListener 返回成功onSuccess(CreatCertResponse)；返回失败onFail(0,msg)
     * @see CreatCertResponse
     */
    public void requestObsCreatCert(int opType, int deviceRegion, String fileName, String ucid, final ObsContract.Callback<CreatCertResponse> busDataListener) {
        CLog.i("sdk-obs", "requestObsCreatCert opType="+opType+",ucid="+ucid);
        new ObsRequestModel().requestObsCreatCert(opType, deviceRegion, fileName, ucid, new IUIDataListener() {
            @Override
            public void uiDataSuccess(Object object) {
                BaseResultWrapper<CreatCertResponse> wrapper = (BaseResultWrapper<CreatCertResponse>) object;
                if(wrapper == null){
                    CLog.e("sdk-obs", "requestObsCreatCert wrapper == null");
                    busDataListener.onFail( 0, "wrapper == null"  );
                    return;
                }

                if (wrapper.code != 1) {
                    CLog.e("sdk-obs", "requestObsCreatCert onFail  code="+wrapper.code);
                    busDataListener.onFail( wrapper.code, wrapper.msg);
                } else {
                    busDataListener.onSuccess(wrapper.data);
                }
            }

            @Override
            public void uiDataError(Exception error) {
                CLog.e("sdk-obs", "requestObsFileSync uiDataError ");
                busDataListener.onFail(0, error == null ? "" : error.getMessage());
            }
        });
    }



    /**
     * 设备订单信息查询
     * @param deviceCodes
     * @param ucid
     * @param busDataListener 返回成功onSuccess(DeviceOrderResponse)；返回失败onFail(0,msg)
     * @see DeviceOrderResponse
     */
    public void requestApiDeviceOrder(String deviceCodes, String ucid, final ObsContract.Callback<List<DeviceOrderResponse>> busDataListener) {
        CLog.i("sdk-obs", "requestApiDeviceOrder "+",ucid="+ucid);
        new ObsRequestModel().requestApiDeviceOrder(deviceCodes, ucid, new IUIDataListener() {
            @Override
            public void uiDataSuccess(Object object) {
                BaseResultWrapper<List<DeviceOrderResponse>> wrapper = (BaseResultWrapper<List<DeviceOrderResponse>>) object;
                if(wrapper == null){
                    CLog.e("sdk-obs", "requestApiDeviceOrder wrapper == null");
                    busDataListener.onFail( 0, "wrapper == null"  );
                    return;
                }

                if (wrapper.code != 1) {
                    CLog.e("sdk-obs", "requestApiDeviceOrder onFail  code="+wrapper.code);
                    busDataListener.onFail( wrapper.code, wrapper.msg);
                } else {
                    busDataListener.onSuccess(wrapper.data);
                }
            }

            @Override
            public void uiDataError(Exception error) {
                CLog.e("sdk-obs", "requestApiDeviceOrder uiDataError ");
                busDataListener.onFail(0, error == null ? "" : error.getMessage());
            }
        });
    }

    public void getThisDeviceOrder(String deviceCodes, String ucid, final ObsContract.Callback<DeviceOrderResponse> busDataListener) {
        if (deviceOrder != null) {
            busDataListener.onSuccess(deviceOrder);
            return;
        }
        requestApiDeviceOrder(deviceCodes, ucid, new ObsContract.Callback<List<DeviceOrderResponse>>() {
            @Override
            public void onSuccess(List<DeviceOrderResponse> data) {
                if (data != null && !data.isEmpty()) {
                    deviceOrder = data.get(0);
                }
                busDataListener.onSuccess(deviceOrder);
            }

            @Override
            public void onFail(int code, String msg) {
                busDataListener.onFail(code, msg);
            }
        });
    }
}
