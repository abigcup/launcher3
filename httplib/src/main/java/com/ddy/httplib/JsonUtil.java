package com.ddy.httplib;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by linbinghuang
 * Date 2015/10/13
 * json解析
 */
public class JsonUtil {
    /**
     * 从JSON字符串中反序列化T对象
     *
     * @param pJsonStr JSON字符串
     * @param pClass 对象的Class
     * @param <T> 将要反序列化成的T对象
     * @return T对象
     */
    public static <T> T parserTFromJson(String pJsonStr, final Class<T> pClass){
        T _T;
        try{
            if (!TextUtils.isEmpty(pJsonStr)){
                Gson _Gson = new Gson();
                _T = _Gson.fromJson(pJsonStr, pClass);
            }else {
                _T = null;
            }
        }catch (Exception e){
            e.printStackTrace();
            _T = null;
        }
        return _T;
    }
    //json变成类
    public static Object parsData(String json, Class clazz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //json变成类(含泛型)
    public static Object parsData(String json, TypeToken type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, type.getType());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
    //json变成类(含泛型)
    public static Object parsData(String json, Type type) {
        try {
            Gson gson = new Gson();
            Object result = gson.fromJson(json, type);
            return result;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
    //json对象转字符串
    public static String objectToString(Object o) {
        try {
            Gson gson = new Gson();
            return gson.toJson(o);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    //json变成类（有集合有泛型）
    public static <T> Object parsListData(String json, Class clazz) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
    /**
     * 时间格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 创建GSON
     * @author:qiuchen
     * @createTime:2012-9-24
     * @return
     */
    public static Gson getGson(){
        return new GsonBuilder().serializeNulls().setDateFormat(DATE_FORMAT).create();
    }
    /**
     * 将字符串数组转化为对象集合
     * @author:qiuchen
     * @createTime:2012-9-24
     * @param <T>
     * @param jsonStr
     * @param tClass
     * @return
     */
    public static <T> List<T> json2Collection(String jsonStr,Class<T> tClass){
        return getGson().fromJson(jsonStr,new TypeToken<List<T>>(){}.getType());
    }

    public static <T> List<T> getObjectList(String jsonString,Class<T> cls){
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Object StringToDate(Object o) {
        try {

            Gson gson = new Gson();
            return gson.toJson(o);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static String class2Data(Object object){
        try {
            Gson gson = new Gson();
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static String class2DataToHtml(Object object){
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
}
