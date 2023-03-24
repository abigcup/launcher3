package com.android.Utils;

import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @author tomchen
 * @date 2019-12-04
 */
public class PropUtils {

    public static final String UNKNOWN = "";
    private static HashMap<String, String> sHashMap = new HashMap<>();

    /**
     * 这里是假定要取的值是重启后，不会发生变化的。如果本身逻辑上是会变化的，不能通过该函数获取
     * @param key
     * @return
     */
    public static String getString(String key) {
        Log.e("getprop","getprop: getString "+key);
        try {
            if (sHashMap.isEmpty() || !sHashMap.containsKey(key)) {
                ShellUtils2.CommandResult getprop = ShellUtils2.execCmd("getprop "+key, false);
                int result = getprop.result;
                if (result == 0) {
                    String value = getprop.successMsg;
                    sHashMap.put(key, value);
                    Log.i("getprop","shell getprop:"+key+","+value);
                    return value;
                } else {
                    Log.e("getprop","getprop:"+getprop.toString());
                    return UNKNOWN;
                }
            } else if (sHashMap.containsKey(key)) {
                String value = sHashMap.get(key);
                //这里尝试再获取，刷新下。 （有出现过getprop phone.id取值为空的情况（但shell环境获取，又有值））
                if(TextUtils.isEmpty(value)){
                    value = getSystemProperty(key);
                    if(!TextUtils.isEmpty(value)){
                        sHashMap.put(key, value);
                    }else{
                        Log.e("getprop",key+": getSystemProperty still empty.");
                    }
                }
                Log.i("getprop","backup getprop:"+key+","+value);
                return value;
            } else {
                Log.e("getprop","not containsKey :"+key);
                return UNKNOWN;
            }
        } catch (Exception e) {
            Log.e("getprop","Exception :"+e.getMessage());
            e.printStackTrace();
            return UNKNOWN;
        }
    }

    private static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    /**
     * 清空数据
     */
    public static void clearData() {
        sHashMap.clear();
    }
}
