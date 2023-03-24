package com.android.Utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by chenmingqun on 2017/11/16.
 */

public class GsonUtil {

    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtil() {
    }

    public static String toJson(Object object) {
        String gsonString = "";
        try {
            if (gson != null) {
                gsonString = gson.toJson(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gsonString;
    }

    public static <T> T fromJson(String jsonData, Class<T> clazz) {
        try {
            if (gson != null) {
                return gson.fromJson(jsonData, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String jsonData, Type typeOfT) {
        try {
            if (gson != null) {
                return gson.fromJson(jsonData, typeOfT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
