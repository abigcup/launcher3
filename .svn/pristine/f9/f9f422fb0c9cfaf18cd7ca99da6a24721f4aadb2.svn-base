package com.ddy.httplib;

import com.blankj.utilcode.util.Utils;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * okhttp工具类，单例呼出Okhttp，防止多个Retrofit建立多个OkHttpClient
 * Created by win7 on 2016/12/11.
 */

public class OkHttpUtils {
    private static final int DEFAULT_TIMEOUT = 30;
    private OkHttpClient mOkHttpClient;
    private OkHttpUtils(){
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
        mBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        mBuilder.sslSocketFactory(createSSLSocketFactory());
        mBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        mBuilder.dns(OkHttpDns.getInstance(Utils.getApp()));
//        new OkHttpClient().newBuilder().proxy(Proxy.NO_PROXY)
//        mOkHttpClient = mBuilder.proxy(Proxy.NO_PROXY).build();
        mOkHttpClient = mBuilder.build();
    }

    public OkHttpClient getOkHttpBuild(){
        return mOkHttpClient;
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final OkHttpUtils INSTANCE = new OkHttpUtils();
    }
    //获取单例
    public static OkHttpUtils getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }
    public static  class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }
}
