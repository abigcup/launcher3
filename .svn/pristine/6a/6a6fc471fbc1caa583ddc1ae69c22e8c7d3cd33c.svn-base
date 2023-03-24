package com.android.hwyun.common.net;

import android.text.TextUtils;
import android.util.Log;

import com.android.hwyun.common.bean.RequestBase;
import com.android.hwyun.common.constants.ConstantsKey;
import com.ddy.httplib.EncryptUtils;
import com.ddy.httplib.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * 真正去后台请求的类
 * 具体可以参考web文档
 * Created by linbaosheng on 2017/4/2.
 */

public class BaseHttpRequest {

    /**
     * 加密后的字符串
     */
    public String Data;

    public Map<String, String> toMapPrames(RequestBase params) throws Exception {
        Log.e("BaseHttpRequest", "toMapPrames: " + JsonUtil.class2Data(params));
        int r = getRandomInt();
        String json = JsonUtil.class2Data(params);
        Data = toDesJson(json);
        String sign = getSign(json, r);
        Map<String, String> map = new HashMap<>();
        map.put("Data", Data);
        map.put("Sign", sign);
        map.put("R", r + "");
        map.put("AppId", params.AppId);
        return map;
    }

    public Map<String, String> toMapPramesByNoEnc(RequestBase params) throws Exception {
        Data = JsonUtil.class2Data(params);
        Map<String, String> map = new HashMap<>();
        map.put("Data", Data);
        map.put("AppId", "81dae64269a24e28bc76f0b27c15e37c");
        return map;
    }

    private int getRandomInt() {
        Random random = new Random();
        return random.nextInt(8);
    }

    private String getSign(String s, int randomint) throws Exception {
        String result = EncryptUtils.sign(s, randomint);
        if (TextUtils.isEmpty(result)) {
            return "";
        }
        return result;
    }

    /**
     * 变成json并des
     * @return
     * @throws Exception
     */
    public String toDesJson(String json) throws Exception {
        if (TextUtils.isEmpty(json)) {
            return "";
        }
        String result = EncryptUtils.encode(json);
        return result;
    }
}
