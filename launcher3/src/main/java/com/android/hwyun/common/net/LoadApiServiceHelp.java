package com.android.hwyun.common.net;

import com.ddy.httplib.RetrofitUtils;

/**
 *
 * Created by linbaosheng on 2016/12/13.
 */

public class LoadApiServiceHelp {

    public static  BaseApiService loadApiService(){
        return RetrofitUtils
                .getInstance()
                .getBaseRetrofit()
                .create(BaseApiService.class);
    }
    public static BaseApiService loadApiService(String baseurl){
        return RetrofitUtils
                .getInstance()
                .getCustomRetrofit(baseurl)
                .create(BaseApiService.class);
    }
}
