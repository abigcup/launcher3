package com.android.launcher3.util;

import android.util.Log;

import com.android.launcher3.BuildConfig;
import com.android.launcher3.bean.HotSeatInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tomchen
 * @date 6/14/22
 */
public class HotSeatMgr {
    public static final String TAG = "HotSeatMgr";

    private Map<String, HotSeatInfo> hotSeatInfoList = new HashMap<>();

    private HotSeatMgr() {
    }

    private static class LazyHolder {
        private static final HotSeatMgr INSTANCE = new HotSeatMgr();
    }

    public static HotSeatMgr getInstance() {
        return LazyHolder.INSTANCE;
    }

    public boolean isHotSeat(String packageName) {
        boolean containsKey = hotSeatInfoList.containsKey(packageName);
        Log.i(TAG, "isHotSeat:" + packageName + "=" + containsKey);
        return containsKey;
    }

    public HotSeatInfo getHotSeatInfo(String packageName) {
        return hotSeatInfoList.get(packageName);
    }

    public void init() throws IOException, XmlPullParserException {
        XmlPullParser parser = SettingConfig.getInstance().getWorkspace();
        int type;
        int depth = parser.getDepth();
        boolean isResolve = false;
        HotSeatInfo hotSeatInfo = null;
        while (((type = parser.next()) != XmlPullParser.END_TAG ||
                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if ("resolve".equals(name)) {
                isResolve = true;
                String container = parser.getAttributeValue(null, "container");
                String screen = parser.getAttributeValue(null, "screen");
                String x = parser.getAttributeValue(null, "x");
                String y = parser.getAttributeValue(null, "y");
                hotSeatInfo = new HotSeatInfo(container, screen, x, y);
            } else if (isResolve && "favorite".equals(parser.getName())) {
                String packageName = parser.getAttributeValue(null, "packageName");
                String className = parser.getAttributeValue(null, "className");
                hotSeatInfo.packageName = packageName;
                hotSeatInfo.className = className;
                hotSeatInfoList.put(packageName, hotSeatInfo);
                isResolve = false;
            }
        }
        if (BuildConfig.DEBUG) {
            for (Map.Entry<String, HotSeatInfo> infoEntry : hotSeatInfoList.entrySet()) {
                Log.i(TAG, "HotSeatMgr init: " + infoEntry.getKey());
            }
        }
    }
}
