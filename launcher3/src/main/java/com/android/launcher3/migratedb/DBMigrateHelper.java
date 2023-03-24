package com.android.launcher3.migratedb;

import android.annotation.SuppressLint;
import android.util.Log;

import com.android.Utils.ShellUtils2;
import com.android.launcher3.Application.MainApplication;
import com.android.launcher3.LauncherFiles;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * Created by suchangxu.
 * Date: 2020/7/20 15:39
 * 数据库迁移到sd卡
 * <p>
 * 无root->目录不存在->无法创建目录->使用沙盒
 * ------—>目录存在->沙盒有文件->拷贝数据库->使用SD卡
 * ---------------->沙盒无文件->直接在SD卡创建数据库->使用SD卡
 * <p>
 * 有root，可以创建目录；无root无法创建目录，其他逻辑无区别
 */
public class DBMigrateHelper {

    private static final String TAG = DBMigrateHelper.class.getSimpleName();

    private static final int FILE_COPY_BUFFER_SIZE = 1024;

    private static class InstanceHolder {

        private static final DBMigrateHelper INSTANCE = new DBMigrateHelper();

    }

    public static DBMigrateHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private final File dbDirectoryFile = new File(LauncherFiles.DB_DIRECTORY_PATH);

    private final File migrationDbFile = new File(LauncherFiles.MIGRATION_DB_PATH);

    private final File sandBoxDirectoryFile = new File(
            MainApplication.getContext().getDatabasePath(LauncherFiles.LAUNCHER_DB).getParentFile().getAbsolutePath()
    );

    private final File sandBoxLauncherFile =
            MainApplication.getContext().getDatabasePath(LauncherFiles.LAUNCHER_DB).getAbsoluteFile();

    private DBMigrateHelper() {
        mkdir();
    }

    private void mkdir() {
        if (!dbDirectoryFile.exists()) {
            Log.i(TAG, "Migrate db exc mkdir");
            ShellUtils2.CommandResult commandResult = ShellUtils2.execCmd(String.format("mkdir -p %s\n", LauncherFiles.DB_DIRECTORY_PATH), true);
            if (commandResult.result == 0) {
                Log.i(TAG, "Migrate db mkdir success");
                modifyAccess(LauncherFiles.CONFIG_DIRECTORY_PATH, true);
            } else {
                Log.e(TAG, "mkdir error code:" + commandResult.result);
            }
        }
    }

    public void migrate() {
        if (sandBoxDirectoryFile.exists() && sandBoxLauncherFile.exists() && !migrationDbFile.exists()) {
            File[] filesInSandBox = sandBoxDirectoryFile.listFiles();
            File dbFileInSdcard;
            File dbFileInSandBox;
            for (int i = 0; i < filesInSandBox.length; i++) {
                dbFileInSandBox = filesInSandBox[i];
                dbFileInSdcard = new File(LauncherFiles.DB_DIRECTORY_PATH + File.separator + dbFileInSandBox.getName());
                if (!dbFileInSdcard.exists() && dbFileInSandBox.exists()) {
                    try {
                        Log.i(TAG, String.format("migrate %s from sandBox to SD card", dbFileInSdcard.getName()));
                        boolean newFile = dbFileInSdcard.createNewFile();
                        if (newFile) {
                            doCopyFile(
                                    dbFileInSandBox,
                                    dbFileInSdcard,
                                    true
                            );
                            modifyAccess(dbFileInSdcard.getAbsolutePath(), false);
                        } else {
                            Log.e(TAG, String.format("migrate %s createNewFile failed", dbFileInSdcard.getName()));
                        }
                    } catch (IOException e) {
                        Log.e(TAG, String.format("migrate %s failed", dbFileInSdcard.getName()));
                    }
                }
            }
            //迁移了就默认已经加载过默认配置
            MigrationConfiguration.Configuration configuration = MigrationConfiguration.read();
            configuration.isLoadedDefaultFavorites = true;
            MigrationConfiguration.write(configuration);
        }
    }

    /**
     * 修改文件权限
     */
    private void modifyAccess(String path, boolean iterate) {
        String command;
        if (iterate) {
            command = String.format("chmod -R 777 %s\n", path);
        } else {
            command = String.format("chmod 777 %s\n", path);
        }
        ShellUtils2.CommandResult commandResult = ShellUtils2.execCmd(command, true);
        if (commandResult.result == 0) {
            Log.i(TAG, "modifyAccess " + path + " success");
        } else {
            Log.e(TAG, "modifyAccess error code:" + commandResult.result);
        }
    }

    private void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            closeQuietly(output);
            closeQuietly(fos);
            closeQuietly(input);
            closeQuietly(fis);
        }
        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
        Log.i(TAG, String.format("Copy %s from sandBox to sd card", srcFile.getName()));
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    public boolean sdDbDirectoryExist() {
        return dbDirectoryFile.exists();
    }

}
