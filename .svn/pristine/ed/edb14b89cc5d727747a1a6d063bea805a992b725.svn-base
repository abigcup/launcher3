package com.android.hwyun.common.net;


import android.text.TextUtils;

import com.ddy.httplib.EncryptUtils;
import com.google.gson.annotations.SerializedName;

/**
 * 解密后的类型
 * 该类主要用来解密后的Gson转换
 * Created by linbaosheng on 2017/4/6.
 */

public class BaseDataResult {

    /**
     * 对应code的描述信息
     */
    @SerializedName(value = "Msg", alternate = "msg")
    public String Msg;
    /**
     * 响应结果代码，0为成功，非0详见msg
     */
    @SerializedName(value = "Code", alternate = "code")
    public Integer Code;
    /**
     * 返回数据的密文
     */
    @SerializedName(value = "Data", alternate = "data")
    public String Data;

    public void setData(){
        if (TextUtils.isEmpty(this.Data)) {
            this.Data = "{}";
            return;
        }
        try {
            this.Data = EncryptUtils.decode(Data);
        }catch (Exception e){
            this.Data = "{}";
        }
    }

    public String getJson(){
        String s = "{\"Msg\":" + "\""+Msg+"\""+ ",\"Code\":" + Code + ",\"Data\":" + Data + "}";
        return s;
    }
}
