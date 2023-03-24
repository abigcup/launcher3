package com.ddy.httplib;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

/**
 * Created by xuwei on 2020/7/1.
 */

public class OkHttpDns implements Dns {
    private static final Dns SYSTEM = Dns.SYSTEM;
    HttpDnsService httpdns;//httpdns 解析服务
    private static OkHttpDns instance = null;
    private OkHttpDns(Context context) {
        this.httpdns = HttpDns.getService(context, "187642");
    }
    public static OkHttpDns getInstance(Context context) {
        if(instance == null) {
            instance = new OkHttpDns(context);
        }
        return instance;
    }
    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        //通过异步解析接口获取ip
        String ip = httpdns.getIpByHostAsync(hostname);
        if(ip != null) {
            //如果ip不为null，直接使用该ip进行网络请求
            List<InetAddress> inetAddresses = Arrays.asList(InetAddress.getAllByName(ip));
            Log.i("OkHttpDns", "inetAddresses:" + inetAddresses);
            return inetAddresses;
        }
        //如果返回null，走系统DNS服务解析域名
        return Dns.SYSTEM.lookup(hostname);
    }
    public void setPreResolveHosts(ArrayList<String> hosts) {
        httpdns.setPreResolveHosts(hosts);
    }
}
