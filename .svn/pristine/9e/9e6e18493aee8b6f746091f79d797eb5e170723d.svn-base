package com.android.hwyun.common.util;

import android.os.Environment;

import com.android.Utils.ShellUtils2;
import com.android.hwyun.common.constants.CommonConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ShellUtils;

import java.io.File;

/**
 * Created by xuwei on 2018/8/30.
 */
public class DownloadUtil {
    public static String getDefaultSavePath(String obsKey) {
        return Environment.getExternalStorageDirectory() + File.separator + AppUtils.getAppPackageName() + File.separator + EncryptUtils.encryptMD5ToString(obsKey);
    }

    public static String getUserFolder(String ucid) {
        return EncryptUtils.encryptMD5ToString(String.format("%s@btgs1215lmjh", ucid)) + "/";
    }

    public static String getApkFolder(String ucid) {
        return getUserFolder(ucid) + "apk/";
    }

    public static String getApkIconPath(String obsKey) {
        int lastSep = obsKey.indexOf("/apk/");
        String folder = lastSep == -1 ? "" : obsKey.substring(0, lastSep + 1);
        String fileName = FileUtils.getFileName(obsKey);
        return folder + "apkinfo/" + fileName + ".png";
    }

    public static int generateId(String url, String path) {
        return generateId(url, path, false);
    }

    public static int generateId(String url, String path, boolean pathAsDirectory) {
        if (pathAsDirectory) {
            return EncryptUtils.encryptMD5ToString(String.format("%sp%s@dir", url, path)).hashCode();
        } else {
            return EncryptUtils.encryptMD5ToString(String.format("%sp%s", url, path)).hashCode();
        }
    }

    public static boolean isInstallSuccess(String strResult) {
        return strResult != null
                && strResult.toLowerCase().contains("success");
    }

    public static String installAppSilent(final File file,
                                    final String params,
                                    final boolean isRooted) {
        if (file == null || !file.exists()) return "error no file";
        String filePath = '"' + file.getAbsolutePath() + '"';
        String command = "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install " +
                (params == null ? "" : params + " ")
                + filePath;
        ShellUtils2.CommandResult commandResult = ShellUtils2.execCmd(command, isRooted);
        if (commandResult.successMsg != null
                && commandResult.successMsg.toLowerCase().contains("success")) {
            LogUtils.eTag("BatchInstallLog", "installAppSilent successMsg: " + commandResult.successMsg +
                    ", errorMsg: " + commandResult.errorMsg);
            return commandResult.successMsg;
        } else {
            LogUtils.eTag("BatchInstallLog", "installAppSilent successMsg: " + commandResult.successMsg +
                    ", errorMsg: " + commandResult.errorMsg);
            return commandResult.errorMsg;
        }
    }

    public static String getBucketName(int bucketType) {
        switch (bucketType) {
            case CommonConstants.BUCKET_TYPE_USERS:
                return CommonConstants.BUCKET_NAME_USERS;
            case CommonConstants.BUCKET_TYPE_APPS :
                return CommonConstants.BUCKET_NAME_APPS;
        }
        return CommonConstants.BUCKET_NAME_USERS;
    }
}
