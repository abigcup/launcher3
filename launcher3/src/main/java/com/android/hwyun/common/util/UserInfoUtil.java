package com.android.hwyun.common.util;

import android.database.Cursor;
import android.net.Uri;

import com.android.launcher3.BuildConfig;
import com.blankj.utilcode.util.Utils;

/**
 * Created by xuwei on 2018/9/10.
 */
public class UserInfoUtil {

    public final static String CONTENT_URI = "content://com.cyjh.content.provider.user.info/person";
    public final static String COLUMN_UCID = "UCID";
    public final static String COLUMN_ORDERID= "OrderId";
    public final static String COLUMN_CHANNEL= "Channel";
    public final static String COLUMN_DDY_VERCODE= "ddyVerCode";
    public final static String COLUMN_COMMENT= "comment";
    public final static String COLUMN_APPKEY= "AppKey";

    public static String getUcid() {
        if(BuildConfig.DEBUG){
            return "8E1B51A51C712AE2";
        }
        String ucid = "";
        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(CONTENT_URI), null, null, null, "personid desc");
        if (cursor != null && cursor.moveToNext()) {
            try {
                ucid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UCID));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        return ucid;
    }

    public static long getOrderID() {
        if(BuildConfig.DEBUG){
            return 10140507;
        }
        long orderID = 0;
        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(CONTENT_URI), null, null, null, "personid desc");
        if (cursor != null && cursor.moveToNext()) {
            try {
                orderID = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ORDERID));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        return orderID;
    }

    public static String getChannel() {
        String channel = "";
        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(CONTENT_URI), null, null, null, "personid desc");
        if (cursor != null && cursor.moveToNext()) {
            try {
                channel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHANNEL));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        return channel;
    }

    public static int getDDYVercode() {
        int ddyVercode = 0;
        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(CONTENT_URI), null, null, null, "personid desc");
        if (cursor != null && cursor.moveToNext()) {
            try {
                ddyVercode = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DDY_VERCODE));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        return ddyVercode;
    }

    public static String getComment() {
        String info = "";
        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(CONTENT_URI), null, null, null, "personid desc");
        if (cursor != null && cursor.moveToNext()) {
            try {
                info = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        return info;
    }

    public static String getSdkAppKey() {
        if(BuildConfig.DEBUG){
            return "B7B38CA38E7DA93E";
        }
        String ret = "";
        Cursor cursor = Utils.getApp().getContentResolver().query(Uri.parse(CONTENT_URI), null, null, null, "personid desc");
        if (cursor != null && cursor.moveToNext()) {
            try {
                ret = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPKEY));
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        return ret;
    }
}
