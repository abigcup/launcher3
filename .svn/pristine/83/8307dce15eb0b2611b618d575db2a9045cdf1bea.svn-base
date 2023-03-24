package com.android.hwyun.statistics;

import android.util.Log;

import com.ddy.httplib.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wzz on 2019/2/14.
 *  优化为启动时化，然后直接获取  20190319 lijy
 */
public class IPUtil {

    private static String ipLine="?";

    public static void init(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取外网ip
                IPUtil.initNetIp();
            }
        }).start();
    }

    public static String getNetIp() {
        synchronized (ipLine) {
            return ipLine;
        }
    }

    /**
     * 获取外网的IP(要访问Url，要放到后台线程里处理)
     *
     * @param @return
     * @return String
     * @throws
     * @Title: GetNetIp
     * @Description:
     */
    private static void initNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        HttpURLConnection httpConnection = null;
        try {
//            infoUrl = new URL("http://ip168.com/");
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    strber.append(line + "\n");
                }
                /**获取{}中的内容包括{} 并转成IpInfo对象*/
                if(strber.indexOf("{") == -1 || strber.indexOf("}") == -1)
                    throw  new Exception("strber not contains {}");

                String str = strber.toString().substring(strber.toString().indexOf("{"),strber.toString().indexOf("}")+1);
                IpInfo ipInfo = (IpInfo) JsonUtil.parsData(str,IpInfo.class);

                if(ipInfo == null)
                    throw new Exception("IpInfo parsdata error.");

                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(strber.toString());
                if (matcher.find()) {
                    //IP地址包括所在的地区名称  如:110.80.153.90(福建省福州市)
                    synchronized (ipLine) {
                        ipLine = matcher.group() + "(" + ipInfo.cname + ")";
                    }
                }

                Log.d(IPUtil.class.getSimpleName(),ipLine);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){//https://bugly.qq.com/v2/crash-reporting/crashes/6ad4b9fec1/51602?pid=1
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                httpConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Log.i(IPUtil.class.getSimpleName(), "getNetIp:"+ipLine);
    }
}
