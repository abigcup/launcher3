package com.android.hwyun.statistics;

import android.util.Log;

import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Created by lijingying on 2019/3/19.
 *  统计接口
 */
public class StatisticsManager {

    public static StatisticsManager manager;

    private ActivityHttpHelper<BaseResultWrapper<String>> httpHelper;

    private IUIDataListener iuiDataListener = new IUIDataListener() {
        @Override
        public void uiDataSuccess(Object object) {
            Log.i("statistics","up data success");
        }

        @Override
        public void uiDataError(Exception error) {
            Log.e("statistics","up data error");
        }
    };

    public static StatisticsManager getInstance() {
        if (manager == null) {
            manager = new StatisticsManager();
        }
        return manager;
    }

    /**
     * 统计
     */
    public void BuriedPoint(int PointCode) {
        BuriedPoint(PointCode, 0);
    }

    public void BuriedPoint(int PointCode, int Orderid) {
        try {
            if (httpHelper == null) {
                TypeToken<BaseResultWrapper<String>> typeToken = new TypeToken<BaseResultWrapper<String>>() {
                };
                httpHelper = new ActivityHttpHelper<>(iuiDataListener, typeToken);
            }

            StatisticsRequestInfo info = new StatisticsRequestInfo();
            info.init();
            info.setPointCode(PointCode);
            info.setOrderId(Orderid);

            Log.i("statistics","send "+ new Gson().toJson(info));

            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            httpHelper.sendPostRequest(HttpConstants.API_BuriedPoint, baseHttpRequest.toMapPramesByNoEnc(info), 30000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
