package com.android.launcher3.util;

import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.google.gson.JsonObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by chenmingqun on 2019/2/18.
 */

public class WorkUtils {
    public static final String TAG = "WorkUtils";

    public static void sendToClient(String command, String commandData) {
        JsonObject response = new JsonObject();
        response.addProperty("type",1);
        response.addProperty("from",6);

        JsonObject data = new JsonObject();
        data.addProperty("code", 1);
        data.addProperty("command", command);
        data.addProperty("type", 5);
        data.addProperty("data", commandData);
        data.addProperty("time", System.currentTimeMillis());
        data.addProperty("version", AppUtils.getAppVersionName());
        response.add("data",data);
        WorkUtils.sendJsonObject(response);
    }

    /**
     * 通过Socket发消息给 中转站 127.0.0.1:  20041
     * @param jsonObject
     */
    public static void sendJsonObject(JsonObject jsonObject) {
        final String ip = "127.0.0.1";
        final int port = 20041;
        sendObjectToServer(jsonObject, ip, port);

    }

    public static void sendObjectToServer(JsonObject jsonObject, final String ip, final int port) {
        final String localMsg = jsonObject.toString();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "GPS send msg: " + localMsg);
                Socket client = null;
                BufferedOutputStream printWriter = null;
                try {
                    client = new Socket(ip, port);
                    printWriter = new BufferedOutputStream(client.getOutputStream());
                    MsgDataBean msgDataBean = new MsgDataBean(localMsg);
                    printWriter.write(msgDataBean.parse());
                    printWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (printWriter != null) {
                            printWriter.close();
                        }
                        if (client != null) {
                            client.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable, "GPS2AppMsg").start();
    }

}
