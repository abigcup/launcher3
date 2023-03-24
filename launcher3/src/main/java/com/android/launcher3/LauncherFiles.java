package com.android.launcher3;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Central list of files the Launcher writes to the application data directory.
 * <p>
 * To add a new Launcher file, create a String constant referring to the filename, and add it to
 * ALL_FILES, as shown below.
 */
public class LauncherFiles {

    private static final String XML = ".xml";

    public static final String DEFAULT_WALLPAPER_THUMBNAIL = "default_thumb2.jpg";
    public static final String DEFAULT_WALLPAPER_THUMBNAIL_OLD = "default_thumb.jpg";
    public static final String SHARED_PREFERENCES_KEY = "com.android.launcher3.prefs";
    public static final String WALLPAPER_CROP_PREFERENCES_KEY =
            "com.android.launcher3.WallpaperCropActivity";
    public static final String MANAGED_USER_PREFERENCES_KEY = "com.android.launcher3.managedusers.prefs";

    //db
    public static final String LAUNCHER_DB = "launcher.db";
    public static final String WALLPAPER_IMAGES_DB = "saved_wallpaper_images.db";
    public static final String WIDGET_PREVIEWS_DB = "widgetpreviews.db";
    public static final String APP_ICONS_DB = "app_icons.db";
    public static final String DB_NAME_TASKS_MANAGER = "tasksmanager.db";
    public static final String PRESET_FOLDER_DB = "preset_folder.db";

    //
    public static final String CONFIG_DIRECTORY_PATH = "/data/local/config";

    //数据库外部路径
    public static final String DB_DIRECTORY_PATH = "/data/local/config/launcher3";

    public static final String MIGRATION_DB_PATH = "/data/local/config/launcher3/" + LAUNCHER_DB;

    //数据库迁移相关配置文件
    public static final String MIGRATION_CONFIGURATION = "/data/local/config/launcher3/migration_configuration.json";

    public static final List<String> ALL_FILES = Collections.unmodifiableList(Arrays.asList(
            DEFAULT_WALLPAPER_THUMBNAIL,
            DEFAULT_WALLPAPER_THUMBNAIL_OLD,
            LAUNCHER_DB,
            SHARED_PREFERENCES_KEY + XML,
            WALLPAPER_CROP_PREFERENCES_KEY + XML,
            WALLPAPER_IMAGES_DB,
            WIDGET_PREVIEWS_DB,
            MANAGED_USER_PREFERENCES_KEY,
            APP_ICONS_DB));

    // TODO: Delete these files on upgrade
    public static final List<String> OBSOLETE_FILES = Collections.unmodifiableList(Arrays.asList(
            "launches.log",
            "stats.log",
            "launcher.preferences",
            "com.android.launcher3.compat.PackageInstallerCompatV16.queue"));
}
