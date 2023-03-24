package com.android.launcher3.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.android.Utils.GsonUtil;
import com.blankj.utilcode.util.FileIOUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by chenmingqun on 2018/4/25.
 */

public class ProcessUtils {

    private static String getProcessName(int pid) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String exception = reader.readLine();
            if (!TextUtils.isEmpty(exception)) {
                exception = exception.trim();
            }

            String var3 = exception;
            return var3;
        } catch (Throwable var13) {
            var13.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

        return null;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static String getRandomProcess(int length) {
        return String.format("com.c%sy.j%sh", getRandomString(3), getRandomString(length));
    }

    private static final String string = "abcdefghijklmnopqrstuvwxyz";

    private static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        int len = string.length();
        for (int i = 0; i < length; i++) {
            sb.append(string.charAt(random.nextInt(len)));
        }
        return sb.toString();
    }

    public static void initProcess(String processName) {
        try {
            Method setter = android.os.Process.class.getMethod("setArgV0", String.class);
            setter.invoke(android.os.Process.class, processName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recordProcessName(ProcessSetting processSetting) {
        String jsonStr = GsonUtil.toJson(processSetting);
        FileIOUtils.writeFileFromString("/data/local/setting/prevInsProcessInfo.json", jsonStr);
    }

    public static ProcessSetting getProcessSetting() {
        String jsonStr = FileIOUtils.readFile2String("/data/local/setting/prevInsProcessInfo.json");
        if (!TextUtils.isEmpty(jsonStr)) {
            return GsonUtil.fromJson(jsonStr, ProcessSetting.class);
        } else {
            return new ProcessSetting();
        }
    }

    public static class ProcessSetting {

        public boolean enable = true;
        public String appName;
        public String packageName;
        public String processName;

    }

}
