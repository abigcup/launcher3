package com.android.launcher3;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.migratedb.LocalDBContext;
import com.android.launcher3.util.Thunk;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by suchangxu.
 * Date: 2020/9/4 13:23
 * 记录预置图标和文件的对应关系，用来支持文件夹禁用功能
 */
public class PresetFolderProvider extends ContentProvider {

    public static final String AUTHORITY = ProviderConfig.AUTHORITY_PRESET_FOLDER;

    private static final int DATABASE_VERSION = 1;

    @Thunk
    PresetFolderProviderChangeListener mListener;
    @Thunk
    PresetFolderProvider.DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new PresetFolderProvider.DatabaseHelper(context);
        return true;
    }

    //查询所有文件夹和图标的关系
    // uri:com.android.launcher3.presetFolder
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(PresetFolder.TABLE_NAME);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor result = qb.query(db, null, null, null, null, null, null);
//        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "com.android.launcher3.presetFolder";
    }

    //插入图标
    // uri:com.android.launcher3.presetFolder/folder_id
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException("values is null");
        }
        long folderId = ContentUris.parseId(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        values.put(PresetFolder.FOLDER_ID, folderId);
        db.insert(PresetFolder.TABLE_NAME, "folder_id", values);
        return uri;
    }

    //删除图标
    // uri:com.android.launcher3.presetFolder/delete_shortcut/package_name
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() != 3 && TextUtils.isEmpty(pathSegments.get(2))) {
            throw new IllegalArgumentException("uri error");
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.delete(PresetFolder.TABLE_NAME, "package_name=?", new String[]{pathSegments.get(2)});
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("no support");
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context) {
            super(
                    LocalDBContext.Factory.newPresetFolderContext(context),
                    LauncherFiles.PRESET_FOLDER_DB, null, DATABASE_VERSION
            );
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE preset_folder (" +
                    "_id INTEGER PRIMARY KEY," +
                    "folder_id INTEGER NOT NULL DEFAULT 0," +
                    "package_name TEXT NOT NULL DEFAULT \"\"" +
                    ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static final class PresetFolder {

        public static final String TABLE_NAME = "preset_folder";

        public static final Uri CONTENT_URI = Uri.parse("content://" +
                ProviderConfig.AUTHORITY_PRESET_FOLDER + "/" + TABLE_NAME);

        public static Uri getInsertUri(long folderId) {
            return Uri.parse("content://" + ProviderConfig.AUTHORITY_PRESET_FOLDER +
                    "/" + TABLE_NAME + "/" + folderId);
        }

        public static Uri getDeleteShortcutUri(String packageName) {
            return Uri.parse("content://" + ProviderConfig.AUTHORITY_PRESET_FOLDER +
                    "/" + TABLE_NAME + "/delete/" + packageName);
        }

        public static final String FOLDER_ID = "folder_id";

        public static final String PACKAGE_NAME = "package_name";

    }


    public interface PresetFolderProviderChangeListener {


    }
}
