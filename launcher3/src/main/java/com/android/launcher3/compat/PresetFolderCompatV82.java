package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.LauncherSettings;
import com.android.launcher3.PresetFolderProvider;
import com.android.launcher3.migratedb.MigrationConfiguration;
import com.blankj.utilcode.util.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by suchangxu.
 * Date: 2020/9/10 12:35
 * 对2.2.82版本的预置图标做兼容。线上版本2.2.82到2.2.91的升级。
 * 原因：2.2.91开始的版本使用了文件夹推荐图标的指令。设计禁用文件夹的功能时，需要一张表来描述预置图标和文件夹的关系。
 * 另，原版的桌面在使用预置图标时，会在favorites.db中插入多条包名相同的预置图标
 * 兼容内容：
 * 1、删除favorites.db中多余的相同包名的预置图标，针对2.2.86版本，过滤版本没在线上使用，但是有问题。
 * 2、将旧的预置图标与{@link com.android.launcher3.LauncherFiles#PRESET_FOLDER_DB}做连接
 */
public class PresetFolderCompatV82 {

    public static void compat(Context base) {
        MigrationConfiguration.Configuration configuration = MigrationConfiguration.read();
        if (!configuration.isPresetShortcutCompatV82Completed) {
            Log.i("PresetFolderCompatV82", "execute PresetFolderCompatV82");

            //删除favorites.db中多余的图标
            List<String> packageNameList = accessConflict(base);

            //和新表建立连接
            linkPresetFolderDb(base, packageNameList);

            configuration.isPresetShortcutCompatV82Completed= true;
            MigrationConfiguration.write(configuration);
        }
    }

    /**
     * 将旧的图标连接到新表中
     *
     * @param base
     * @param packageNameList
     */
    private static void linkPresetFolderDb(Context base, List<String> packageNameList) {
        ContentResolver contentResolver = base.getContentResolver();
        ContentValues contentValues;
        for (String packageName : packageNameList) {
            contentValues = new ContentValues();
            contentValues.put(PresetFolderProvider.PresetFolder.PACKAGE_NAME, packageName);
            contentResolver.insert(PresetFolderProvider.PresetFolder.getInsertUri(0), contentValues);
        }
    }

    /**
     * 处理旧版本预置图标冲突
     *
     * @return 预置图标的包名
     */
    private static List<String> accessConflict(Context base) {
        List<String> packageNameList = new ArrayList<>();
        Map<String, List<Long>> packageNameMapShortcutList = new HashMap<>();
        ContentResolver contentResolver = base.getContentResolver();
        Cursor cursor = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI, null, null, null, null);

        //获取包名和预置图标id的map
        try {
            if (cursor != null) {
                //id,item_type,packageName
                final int idIndex = cursor.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
                final int intentIndex = cursor.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.INTENT);
                final int itemTypeIndex = cursor.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.ITEM_TYPE);
                long id;
                String intentDescription;
                int itemType;
                Intent intent;
                ComponentName cn;
                List<Long> idList;
                while (cursor.moveToNext()) {
                    id = cursor.getLong(idIndex);
                    intentDescription = cursor.getString(intentIndex);
                    itemType = cursor.getInt(itemTypeIndex);
                    if (itemType != LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                        continue;
                    }
                    intent = Intent.parseUri(intentDescription, 0);
                    cn = intent.getComponent();
                    if (cn != null && !TextUtils.isEmpty(cn.getPackageName())) {
                        idList = packageNameMapShortcutList.get(cn.getPackageName());
                        if (idList == null) {
                            idList = new ArrayList<>();
                            packageNameMapShortcutList.put(cn.getPackageName(), idList);
                        }
                        idList.add(id);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        //找出多余的图标，从2.2.86的代码来看，最后一个是当前的图标（移动或删除有效的那个），所以保留最后一个
        List<Long> deleteIdList = new ArrayList<>();
        Set<String> keySet = packageNameMapShortcutList.keySet();
        List<Long> idList;
        for (String packageName : keySet) {
            packageNameList.add(packageName);
            idList = packageNameMapShortcutList.get(packageName);
            if (idList != null && idList.size() > 1) {
                for (int i = 0; i < idList.size() - 1; i++) {
                    deleteIdList.add(idList.get(i));
                }
            }
        }

        //删除多余的图标
        for (Long id : deleteIdList) {
            contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI, "_id=?", new String[]{String.valueOf(id)});
        }

        return packageNameList;
    }

}
