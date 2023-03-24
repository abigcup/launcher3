package com.android.launcher3.migratedb;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.launcher3.LauncherFiles;

import java.io.File;

/**
 * Created by suchangxu.
 * Date: 2020/7/20 17:33
 */
public class LocalDBContext extends ContextWrapper {

    private String dbName;

    private LocalDBContext(Context base, String dbName) {
        super(base);
        this.dbName = dbName;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        if (DBMigrateHelper.getInstance().sdDbDirectoryExist()) {
            Log.e("LocalDBContext", String.format("Use db %s from sd card without errorHandler", name));
            return SQLiteDatabase.openOrCreateDatabase(getDbPath(), factory);
        }
        Log.e("LocalDBContext", String.format("Use db %s from sandBox without errorHandler", name));
        return super.openOrCreateDatabase(name, mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        if (DBMigrateHelper.getInstance().sdDbDirectoryExist()) {
            Log.e("LocalDBContext", String.format("Use db %s from sd card with errorHandler", name));
            return SQLiteDatabase.openOrCreateDatabase(getDbPath(), factory, errorHandler);
        }
        Log.e("LocalDBContext", String.format("Use db %s from sandBox with errorHandler", name));
        return super.openOrCreateDatabase(name, mode, factory, errorHandler);
    }

    private String getDbPath() {
        return LauncherFiles.DB_DIRECTORY_PATH + File.separator + dbName;
    }

    //bugly没有迁移，ut.db没有迁移，没找到ut.db的位置
    public static class Factory {

        public static LocalDBContext newLauncherContext(Context context) {
            return new LocalDBContext(context, LauncherFiles.LAUNCHER_DB);
        }

        public static LocalDBContext newAppIconsContext(Context context) {
            return new LocalDBContext(context, LauncherFiles.APP_ICONS_DB);
        }

        public static LocalDBContext newTasksManagerContext(Context context) {
            return new LocalDBContext(context, LauncherFiles.DB_NAME_TASKS_MANAGER);
        }

        public static LocalDBContext newWallPagerImagesContext(Context context) {
            return new LocalDBContext(context, LauncherFiles.WALLPAPER_IMAGES_DB);
        }

        public static LocalDBContext newWidgetPreviewsContext(Context context) {
            return new LocalDBContext(context, LauncherFiles.WIDGET_PREVIEWS_DB);
        }

        public static LocalDBContext newPresetFolderContext(Context context) {
            return new LocalDBContext(context, LauncherFiles.PRESET_FOLDER_DB);
        }

    }

}
