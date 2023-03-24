package com.android.hwyun.common.util;

import android.text.TextUtils;

import com.android.Utils.PropUtils;
import com.android.launcher3.BuildConfig;
import com.android.Utils.ShellUtils2;
import com.blankj.utilcode.util.ShellUtils;

/**
 * Created by xuwei on 2018/12/17.
 */
public class PhoneIDUtil {

    private String phoneID;

    public String getPhoneID() {
        if (BuildConfig.DEBUG) {
            return "f8f294d3b7b24e688437908ff77ad2a2";
        }
        if (TextUtils.isEmpty(phoneID)) {
            phoneID = PropUtils.getString("phone.id");
        }
        return phoneID;
    }

    private PhoneIDUtil() {

    }

    private static class LazyHolder {
        private static final PhoneIDUtil INSTANCE = new PhoneIDUtil();
    }

    public static PhoneIDUtil getInstance() {
        return LazyHolder.INSTANCE;
    }
}
