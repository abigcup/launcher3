package com.android.hwyun.common.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


/**
 * 控制台输出专用类
 *
 * @author zengyingzhi
 */
public final class CLog {

        private static final boolean mIsDebug = true;
//    private static final boolean mIsDebug = BuildConfig.DEBUG;

    private static final boolean mLogDebug = true;
//    private static final boolean mLogDebug = BuildConfig.DEBUG;

    public static final String LOG_STRING_ZYZ = "SHADOW_ZENG";

    public static boolean isDebug() {
        return mIsDebug;
    }

    public static boolean logDebug() {
        return mLogDebug;
    }

    public static void v(String TAG, String msg) {
        if (mLogDebug) {
            Log.v(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (mLogDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (mLogDebug) {
            Log.w(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (mLogDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (mLogDebug) {
//            Log.i(TAG, msg + "===" +  DateUtil.DateToStr(new Date()
//                , DateUtil.LONG_DATE_FORMAT_2));
            Log.i(TAG, msg);
        }
    }

    public static void error(String msg) {
        Log.e(LOG_STRING_ZYZ, msg);
    }

    public static void sysout(String msg) {
        if (mIsDebug) {
            System.out.println(msg);
        }
    }

    public static void sysoutAppend(String msg) {
        if (mIsDebug) {
            System.out.print(msg);
        }
    }

    public static void toastSysShort(Context context, String msg) {
        if (mIsDebug) {
            Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
        }
    }

    public static void toastSysLong(Context context, String msg) {
        if (mIsDebug) {
            Toast.makeText(context, msg + "", Toast.LENGTH_LONG).show();
        }
    }

    public static void toastMyTime(Context context, String msg, int time) {
        if (mIsDebug) {
            Toast.makeText(context, msg + "", time).show();
        }
    }

//    public static void logAnbox(String tag, String msg) {
//        if (mIsDebug) {
//            Log.i("" + tag, msg);
//            FileUtils.writeFile(Constants.FENGWO_FILE + "anbox_log.txt", "tag:" + tag +
//                    ",msg:" + msg + "》" + DateUtil.DateToStr(new Date(), DateUtil
//                    .LONG_DATE_FORMAT_2) + "\n\r", true);
//        }
//    }
//
//    public static void recordWebsPath(String msg1, String msg2, String msg3) {
//        if (mIsDebug) {
//            FileUtils.writeFile(Constants.FENGWO_FILE + "anbox_log.txt", msg1 + " " + msg2 + " " + msg3, true);
//        }
//    }
}
