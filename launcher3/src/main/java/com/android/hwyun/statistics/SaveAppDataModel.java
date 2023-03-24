package com.android.hwyun.statistics;


import android.util.Log;

import com.android.hwyun.common.bean.RequestBase;
import com.android.hwyun.common.constants.HttpConstants;
import com.android.hwyun.common.net.ActivityHttpHelper;
import com.android.hwyun.common.net.BaseHttpRequest;
import com.android.hwyun.common.net.BaseResultWrapper;
import com.android.hwyun.common.net.NomalConstans;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.android.hwyun.common.util.PhoneIDUtil;
import com.android.hwyun.common.util.UserInfoUtil;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.reflect.TypeToken;


/**
 * Created by lijy on 2019/3/20.
 *  该应用启动，安装数据。通过app站点继续统计。而非新的data.ddyun123.com站点统计的。
 *  http://app.ddyun123.com/Help/Api/POST-AppMarket-SaveAppInstallData
 */
public class SaveAppDataModel {
    public static class RequestSaveAppData extends RequestBase{
        /**
         * AppName : sample string 1
         * AppPackageName : sample string 2
         * PhoneId : sample string 3
         * OrderId : 4
         * AppSource : 5
         */
        private String AppName;
        private String AppPackageName;
        private String PhoneId;
        private long OrderId;
        private int AppSource;

        public void init(){
            PhoneId =  PhoneIDUtil.getInstance().getPhoneID();
            OrderId = UserInfoUtil.getOrderID();
            AppSource = 1;
        }

        public String getAppName() {
            return AppName;
        }

        public void setAppName(String AppName) {
            this.AppName = AppName;
        }

        public String getAppPackageName() {
            return AppPackageName;
        }

        public void setAppPackageName(String AppPackageName) {
            this.AppPackageName = AppPackageName;
        }

        public String getPhoneId() {
            return PhoneId;
        }

        public void setPhoneId(String PhoneId) {
            this.PhoneId = PhoneId;
        }

        public long getOrderId() {
            return OrderId;
        }

        public void setOrderId(long OrderId) {
            this.OrderId = OrderId;
        }

        public int getAppSource() {
            return AppSource;
        }

        public void setAppSource(int AppSource) {
            this.AppSource = AppSource;
        }
    }

    /**
     * 应用启动
     */
    private ActivityHttpHelper<BaseResultWrapper<String>> startHttpHelper;

    /**
     * 安装数据
     */
    private ActivityHttpHelper<BaseResultWrapper<String>> installHttpHelper;

    /**
     * 应用启动
     */
    public void statisticsAppStart(final  String AppPackageName) {
        try {
            if (startHttpHelper == null) {
                TypeToken<BaseResultWrapper<String>> typeToken = new TypeToken<BaseResultWrapper<String>>() {
                };
                startHttpHelper = new ActivityHttpHelper<>(new IUIDataListener() {
                    @Override
                    public void uiDataSuccess(Object object) {
                        Log.i("statistics","uiDataSuccess");
                    }

                    @Override
                    public void uiDataError(Exception error) {
                        LogUtils.eTag("statistics","uiDataError");
                    }
                }, typeToken);
            }

            RequestSaveAppData request = new RequestSaveAppData();
            request.init();
            request.setAppName(AppUtils.getAppName(AppPackageName));
            request.setAppPackageName(AppPackageName);

            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            startHttpHelper.sendPostRequest(HttpConstants.APPMARKET_SAVE_APPS_START, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            LogUtils.eTag("statistics",e.getMessage());
        }
    }
    /**
     * 应用安装
     */
    public void statisticsAppInstall(final  String AppPackageName) {
        try {
            if (installHttpHelper == null) {
                TypeToken<BaseResultWrapper<String>> typeToken = new TypeToken<BaseResultWrapper<String>>() {
                };
                installHttpHelper = new ActivityHttpHelper<>(new IUIDataListener() {
                    @Override
                    public void uiDataSuccess(Object object) {
                        Log.i("statistics","uiDataSuccess");
                    }

                    @Override
                    public void uiDataError(Exception error) {
                        LogUtils.eTag("statistics","uiDataError");
                    }
                }, typeToken);
            }

            RequestSaveAppData request = new RequestSaveAppData();
            request.init();
            request.setAppName(AppUtils.getAppName(AppPackageName));
            request.setAppPackageName(AppPackageName);

            BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
            installHttpHelper.sendPostRequest(HttpConstants.APPMARKET_SAVE_APPS_INSTALL, baseHttpRequest.toMapPrames(request), NomalConstans.TIME_OUT);
        } catch (Exception e) {
            LogUtils.eTag("statistics",e.getMessage());
        }
    }
}
