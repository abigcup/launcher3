package com.android.hwyun.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.Utils.PropUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.ddy.httplib.OkHttpDns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by xuwei on 2020/4/15.
 *
 * {
 *     "readme":"migu-test-ddyun",
 *     "baseDdy":"http://ecapp.ddyun123.net:18080",
 *     "baseDapi":"http://ecdapi.ddyun123.net:18080",
 *     "baseData":"http://ecdata.ddyun123.net:18080",
 *     "baseObs":"http://ecobs.ddyun123.net",
 *     "extend":[
 *         {
 *             "phoneIds":"xxxx,xxxxxx,",
 *             "readme":"migu-test-ddyun",
 *             "baseDdy":"http://ecapp.ddyun123.net:18080",
 *             "baseDapi":"http://ecdapi.ddyun123.net:18080",
 *             "baseData":"http://ecdata.ddyun123.net:18080",
 *             "baseObs":"http://ecobs.ddyun123.net"
 *         },
 *         {
 *             "phoneIds":"xxxx,xxxxxx,",
 *             "readme":"migu-test-ddyun",
 *             "baseDdy":"http://ecapp.ddyun123.net:18080",
 *             "baseDapi":"http://ecdapi.ddyun123.net:18080",
 *             "baseData":"http://ecdata.ddyun123.net:18080",
 *             "baseObs":"http://ecobs.ddyun123.net"
 *         }
 *     ]
 * }
 */
public class DomainUtils {
//    public static final String BASE_DDY = (!BuildConfig.IS_SDK_YUNGAME )? "http://app.ddyun123.com" : "http://app_game.ddyun.com";
//    public static final String BASE_DAPI = (!BuildConfig.IS_SDK_YUNGAME )? "http://dapi.ddyun.com" : "http://dapi_game.ddyun.com";
//    public static final String BASE_DATA = (!BuildConfig.IS_SDK_YUNGAME) ? "http://data.ddyun.com" : "http://data_game.ddyun.com";

    //(BuildConfig.IS_ZSC ? ((!BuildConfig.IS_SDK_YUNGAME )? "http://117.27.139.231:8099" : "http://117.27.139.231:6161") : "http://222.76.112.89:5858"))
    public static final String API_BASE_URL_DDY = DomainUtils.getBaseDomain("baseDdy", "http://app.ddyun.com");

    //(BuildConfig.IS_ZSC ? ((!BuildConfig.IS_SDK_YUNGAME )? "http://117.27.139.231:5762" : "http://117.27.139.231:6165") : "http://222.76.112.89:8110")) ;
    public static final String API_BASE_URL_DAPI = DomainUtils.getBaseDomain("baseDapi", "http://dapi.ddyun.com");

    //(BuildConfig.IS_ZSC ? "http://117.27.139.231:8901" : "http://222.76.112.89:8081")) ;
    public static final String API_BASE_URL_DATA = DomainUtils.getBaseDomain("baseData", "http://data.ddyun.com");

    //(BuildConfig.FLAVOR.contains("_test_product") ? "obs.ddyun.com" : "222.76.112.89:8084"));
    public static final String API_BASE_URL_OBS = DomainUtils.getBaseDomain("baseObs", "http://obs.ddyun.com");
    //http://222.76.112.89:8087
    public static final String API_BASE_URL_STORAGE = DomainUtils.getBaseDomain("baseStorage", "http://storage.ddyun.com");

    private static JSONObject domainInfo;

    public static String getBaseDomain(String key, String defaultValue) {
        if (domainInfo == null) {
            String strInfo = FileIOUtils.readFile2String("/data/local/setting/ddydomain.txt");
            if (!TextUtils.isEmpty(strInfo)) {
                try {
                    domainInfo = new JSONObject(strInfo);

                    //有扩展的，可根据phoneid来特别制定另一个域名
                    if(domainInfo.has("extend")){
                        String phoneId = PropUtils.getString("phone.id");
                        Log.i(DomainUtils.class.getSimpleName(), "get domain phoneId " + phoneId);
                        if(!phoneId.isEmpty()) {
                            try {
                                JSONArray extend = domainInfo.getJSONArray("extend");
                                for (int index = 0; index < extend.length(); ++index) {
                                    JSONObject object = extend.getJSONObject(index);

                                    String phoneIds = object.getString("phoneIds");
                                    if (phoneIds.contains(phoneId)) {
                                        Log.i(DomainUtils.class.getSimpleName(), "get domain extend " + phoneId);

                                        domainInfo = object;
                                        break;
                                    }
                                }
                            }catch (JSONException ex){

                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(DomainUtils.class.getSimpleName(), "get domain " + key + " error " + e.getMessage());
                }
            }
        }
        if (domainInfo != null) {
            try {
                return domainInfo.get(key).toString();
            } catch (Exception e) {
                Log.e(DomainUtils.class.getSimpleName(), "get domain " + key + " error " + e.getMessage());
            }
        }
        Log.e(DomainUtils.class.getSimpleName(), "get domain " + key + " error");
        return defaultValue;
    }

    public static void initHTTPDNS(Context context) {
        ArrayList<String> hostList = new ArrayList<>();
        addUrlHost(hostList, API_BASE_URL_DDY);
        addUrlHost(hostList, API_BASE_URL_DAPI);
        addUrlHost(hostList, API_BASE_URL_DATA);
        addUrlHost(hostList, API_BASE_URL_OBS);
        OkHttpDns.getInstance(context).setPreResolveHosts(hostList);
    }

    private static void addUrlHost(ArrayList<String> hostList, String strUrl) {
        String host = "";
        try {
            URL url = new URL(strUrl);
            host = url.getHost();
        } catch (Exception e) {
        }
        if (!TextUtils.isEmpty(host)) {
            hostList.add(host);
        }
    }
}

