package com.android.hwyun.common.util;

import java.util.List;

/**
 * Created by xuwei on 2018/8/28.
 */
public class Utils {
    public  static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.2f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format("%.2f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format("%.2f KB", f);
        } else
            return String.format("%d B", size);
    }

    public  static String convertSpeedSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB/s", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format("%.1f MB/s", f);
        } else if (size >= kb) {
            long f = size / kb;
            return String.format("%d KB/s", f);
        } else
            return String.format("%d B/s", size);
    }

    public static int getListSize(List list) {
        if (list == null) {
            return 0;
        }
        return list.size();
    }
}
