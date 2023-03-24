package com.android.launcher3.migratedb;

import android.util.Log;

import com.android.Utils.GsonUtil;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherFiles;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by suchangxu.
 * Date: 2020/8/21 12:57
 * 迁移相关的配置类
 */
public class MigrationConfiguration {

    public static boolean write(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must not be null");
        }
        File file = new File(LauncherFiles.MIGRATION_CONFIGURATION);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            Gson gson = new Gson();
            String configurationJson = gson.toJson(configuration);
            writer.write(configurationJson);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static Configuration read() {
        File file = new File(LauncherFiles.MIGRATION_CONFIGURATION);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            Configuration configuration = gson.fromJson(fileReader, Configuration.class);
            if (configuration == null) {
                configuration = new Configuration();
            }
            return configuration;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Configuration();
    }


    //配置bean
    public static class Configuration {

        //标记是否加载过默认的桌面配置
        public boolean isLoadedDefaultFavorites = false;

        public boolean isPresetShortcutCompatV82Completed = false;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

}
