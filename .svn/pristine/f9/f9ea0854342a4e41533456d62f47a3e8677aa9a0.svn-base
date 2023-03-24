package com.ddy.httplib;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Retrofit工具类，主要用于装载OkHttpUtil
 * Created by win7 on 2016/12/12.
 */

public class RetrofitUtils {

    private RetrofitUtils(){}

    /**
     * 获取自定义地址的Retrofit
     * @param baseUrl
     * @return
     */
    public Retrofit getCustomRetrofit(String baseUrl){
        return new Retrofit.Builder()
                .client(OkHttpUtils.getInstance().getOkHttpBuild())
//                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public Retrofit getBaseRetrofit(){
        return new Retrofit.Builder()
                .client(OkHttpUtils.getInstance().getOkHttpBuild())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .baseUrl(API_BASE_URL)
                .build();
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final RetrofitUtils INSTANCE = new RetrofitUtils();
    }
    //获取单例
    public static RetrofitUtils getInstance(){
        return SingletonHolder.INSTANCE;
    }
}
