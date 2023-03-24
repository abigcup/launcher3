package com.android.hwyun.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import com.android.launcher3.Application.MainApplication;
import com.android.launcher3.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @author Comsys-linbinghuang
 * @ClassName: SharepreferenceUtils
 * @Description: 开发完毕要并入SharepreferenceUtil
 * @date 2015-1-14 上午10:29:28
 */
public class SharepreferenceUtils {

    /**
     * 用SharePreferences保存类
     *
     * @param @param context
     * @param @param fileName
     * @param @param nodeName
     * @param @param list
     * @return void
     * @throws
     * @Description:
     */
    public static <T> void saveClass(Context context, String fileName, String nodeName, T list) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(list);
            String listBase64 = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
//            SharepreferenceUtil.putSharePreStr(context, fileName, nodeName, listBase64);
            MySharepreferenceUtil.putSharePreStr(context, fileName, nodeName, listBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * jsonToClass
     *
     * @param @param  string
     * @param @return
     * @return T
     * @throws
     * @Description: json串转成类
     */
    public static <T> T jsonToClass(String string) {
        try {
            byte[] mBytes = Base64.decode(string.getBytes(), Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mBytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            T list = (T) objectInputStream.readObject();
            objectInputStream.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T jsonToClass(Context context, String fileName, String nodeName) {
        try {
            String json = /*SharepreferenceUtil*/MySharepreferenceUtil.getSharePreString(context, fileName, nodeName, "");
            if (json.equals("")) {
                return null;
            }
            return jsonToClass(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 删除sp中的某个值
     *
     * @param value
     */
    public static void removeValueToString(String value) {
        Editor editor = getVideoSP().edit();
        editor.remove(value);
        editor.commit();
    }


    public static SharedPreferences getVideoSP() {
        return MainApplication.getInstance().getSharedPreferences(MainApplication.getInstance().getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public static void setSharedPreferencesToString(String key, String value) {
        Editor editor = getVideoSP().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setSharedPreferencesToLong(String key, long value) {
        Editor editor = getVideoSP().edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void setSharePreferencesToBoolean(String key, boolean value) {
        Editor editor = getVideoSP().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setSharePreferencesToInt(String key, int value) {
        Editor editor = getVideoSP().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getSharedPreferencesToInt(String key, int value) {
        return getVideoSP().getInt(key, value);
    }

    public static String getSharedPreferencesToString(String key, String value) {
        return getVideoSP().getString(key, value);
    }

    public static long getSharedPreferencesToLong(String key, long value) {
        return getVideoSP().getLong(key, value);
    }

    public static boolean getSharedPreferencesToBoolean(String key, boolean value) {
        return getVideoSP().getBoolean(key, value);
    }

    public static boolean saveArray(String key, ArrayList<String> aList) {
        Editor mEdit1 = getVideoSP().edit();
        mEdit1.putInt(key + "_size", aList.size()); /* sKey is an array */
        for (int i = 0; i < aList.size(); i++) {
            mEdit1.remove(key + "_" + i);
            mEdit1.putString(key + "_" + i, aList.get(i));
        }

        return mEdit1.commit();
    }

    public static ArrayList<String> getArray(String key) {
        SharedPreferences mSharedPreference1 = getVideoSP();
        ArrayList<String> aList = new ArrayList<String>();
        int size = mSharedPreference1.getInt(key + "_size", 0);
        for (int i = 0; i < size; i++) {
            aList.add(mSharedPreference1.getString(key + "_" + i, null));
        }
        return aList;
    }


//	public static  Object jsonToClass(Context context, String fileName, String nodeName, Class clazz) {
//		String json = SharepreferenceUtil.getSharePreString(context, fileName, nodeName, "");
//		return json != null && !json.equals("")?JsonUtil.parsData(json,clazz):null;
//	}
//	public static  Object jsonToClass(Context context, String fileName, String nodeName, TypeToken type) {
//		String json = SharepreferenceUtil.getSharePreString(context, fileName, nodeName, "");
//		return json != null && !json.equals("")?JsonUtil.parsData(json,type):null;
//	}
//	public static  void saveClass(Context context, String fileName, String nodeName, Object object) {
//		try {
//			SharepreferenceUtil.putSharePreStr(context, fileName, nodeName, JsonUtil.objectToString(object));
//		} catch (Exception var7) {
//			var7.printStackTrace();
//		}
//	}
}
