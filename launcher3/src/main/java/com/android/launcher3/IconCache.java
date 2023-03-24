/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.bean.DesktopIconInfo;
import com.android.launcher3.compat.LauncherActivityInfoCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserHandleCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.migratedb.LocalDBContext;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.Thunk;
import com.blankj.utilcode.util.FileUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Cache of application icons.  Icons can be made from any thread.
 */
public class IconCache {

    private static final String TAG = "Launcher.IconCache";

    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;

    // Empty class name is used for storing package default entry.
    private static final String EMPTY_CLASS_NAME = ".";

    private static final boolean DEBUG = false;

    private static final int LOW_RES_SCALE_FACTOR = 5;

    @Thunk
    static final Object ICON_UPDATE_TOKEN = new Object();

    @Thunk
    static class CacheEntry {
        public Bitmap icon;
        public CharSequence title = "";
        public CharSequence contentDescription = "";
        public boolean isLowResIcon;
    }

    private final HashMap<UserHandleCompat, Bitmap> mDefaultIcons = new HashMap<>();
    @Thunk
    final MainThreadExecutor mMainThreadExecutor = new MainThreadExecutor();

    private final Context mContext;
    private final PackageManager mPackageManager;
    @Thunk
    final UserManagerCompat mUserManager;
    private final LauncherAppsCompat mLauncherApps;
    private final HashMap<ComponentKey, CacheEntry> mCache =
            new HashMap<ComponentKey, CacheEntry>(INITIAL_ICON_CACHE_CAPACITY);
    private final int mIconDpi;
    private float mIconScale = 1f;// 默认图标长宽相等
    @Thunk
    final IconDB mIconDb;

    @Thunk
    final Handler mWorkerHandler;

    // The background color used for activity icons. Since these icons are displayed in all-apps
    // and folders, this would be same as the light quantum panel background. This color
    // is used to convert icons to RGB_565.
    private final int mActivityBgColor;
    // The background color used for package icons. These are displayed in widget tray, which
    // has a dark quantum panel background.
    private final int mPackageBgColor;
    private final BitmapFactory.Options mLowResOptions;

    private String mSystemState;
    private Bitmap mLowResBitmap;
    private Canvas mLowResCanvas;
    private Paint mLowResPaint;
    // 配置：
    private boolean mEnableIconRoundCorner; // 图标圆角开关
    private boolean mEnableIconBackgroundColor; // 图标背景开关
    private boolean mEnableIconReplace; // 图标替换开关
    private int mIconRoundCornerSize; // 圆角大小
    private int mIconBackgroundColor; // 图标背景颜色
    private List<DesktopIconInfo.AppsdetailsBean> appsdetails;// 配置的应用图标和名称列表
    private Map<String, DesktopIconInfo.AppsdetailsBean> pkgAppDetailsMap = new HashMap<>();

    public IconCache(Context context, InvariantDeviceProfile inv) {
        mContext = context;
        mPackageManager = context.getPackageManager();
        mUserManager = UserManagerCompat.getInstance(mContext);
        mLauncherApps = LauncherAppsCompat.getInstance(mContext);
        mIconDpi = inv.fillResIconDpi;
        mIconDb = new IconDB(context);
        mEnableIconRoundCorner = inv.enableIconRoundCorner;
        mEnableIconBackgroundColor = inv.enableIconBackgroundColor;
        mEnableIconReplace = inv.enableIconReplace;
        appsdetails = inv.appsdetails;
        for (DesktopIconInfo.AppsdetailsBean item : appsdetails) {
            if (item != null && !TextUtils.isEmpty(item.pkg)) {
                pkgAppDetailsMap.put(item.pkg, item);
            }
        }
        mIconBackgroundColor = inv.iconBackgroundColor;
        mIconRoundCornerSize = inv.iconRoundCornerSize;

        mWorkerHandler = new Handler(LauncherModel.getWorkerLooper());

        mActivityBgColor = context.getResources().getColor(R.color.quantum_panel_bg_color);
        mPackageBgColor = context.getResources().getColor(R.color.quantum_panel_bg_color_dark);
        mLowResOptions = new BitmapFactory.Options();
        // Always prefer RGB_565 config for low res. If the bitmap has transparency, it will
        // automatically be loaded as ALPHA_8888.
        mLowResOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        updateSystemStateString();
    }

    private Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
    }

    private Drawable getFullResIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            d = resources.getDrawableForDensity(iconId, mIconDpi);
        } catch (Resources.NotFoundException e) {
            d = null;
        }

        return (d != null) ? d : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String packageName, int iconId) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(ActivityInfo info) {
        Resources resources;
        try {
            resources = mPackageManager.getResourcesForApplication(
                    info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getFullResIcon(resources, iconId);
            }
        }

        return getFullResDefaultActivityIcon();
    }

    private Bitmap makeDefaultIcon(UserHandleCompat user) {
        Drawable unbadged = getFullResDefaultActivityIcon();
        Drawable d = mUserManager.getBadgedDrawableForUser(unbadged, user);
        Bitmap b = Bitmap.createBitmap(Math.max(d.getIntrinsicWidth(), 1),
                Math.max(d.getIntrinsicHeight(), 1),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        d.setBounds(0, 0, b.getWidth(), b.getHeight());
        d.draw(c);
        c.setBitmap(null);
        return b;
    }

    /**
     * Remove any records for the supplied ComponentName.
     */
    public synchronized void remove(ComponentName componentName, UserHandleCompat user) {
        mCache.remove(new ComponentKey(componentName, user));
    }

    /**
     * Remove any records for the supplied package name from memory.
     */
    private void removeFromMemCacheLocked(String packageName, UserHandleCompat user) {
        HashSet<ComponentKey> forDeletion = new HashSet<ComponentKey>();
        for (ComponentKey key : mCache.keySet()) {
            if (key.componentName.getPackageName().equals(packageName)
                    && key.user.equals(user)) {
                forDeletion.add(key);
            }
        }
        for (ComponentKey condemned : forDeletion) {
            mCache.remove(condemned);
        }
    }

    /**
     * Updates the entries related to the given package in memory and persistent DB.
     */
    public synchronized void updateIconsForPkg(String packageName, UserHandleCompat user) {
        removeIconsForPkg(packageName, user);
        try {
            PackageInfo info = mPackageManager.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            long userSerial = mUserManager.getSerialNumberForUser(user);
            for (LauncherActivityInfoCompat app : mLauncherApps.getActivityList(packageName, user)) {
                addIconToDBAndMemCache(app, info, userSerial);
            }
        } catch (NameNotFoundException e) {
            Log.d(TAG, "Package not found", e);
            return;
        }
    }

    /**
     * Removes the entries related to the given package in memory and persistent DB.
     */
    public synchronized void removeIconsForPkg(String packageName, UserHandleCompat user) {
        removeFromMemCacheLocked(packageName, user);
        long userSerial = mUserManager.getSerialNumberForUser(user);
        mIconDb.getWritableDatabase().delete(IconDB.TABLE_NAME,
                IconDB.COLUMN_COMPONENT + " LIKE ? AND " + IconDB.COLUMN_USER + " = ?",
                new String[]{packageName + "/%", Long.toString(userSerial)});
    }

    public void updateDbIcons(Set<String> ignorePackagesForMainUser) {
        // Remove all active icon update tasks.
        mWorkerHandler.removeCallbacksAndMessages(ICON_UPDATE_TOKEN);

        updateSystemStateString();
        for (UserHandleCompat user : mUserManager.getUserProfiles()) {
            // Query for the set of apps
            final List<LauncherActivityInfoCompat> apps = mLauncherApps.getActivityList(null, user);
            // Fail if we don't have any apps
            // TODO: Fix this. Only fail for the current user.
            if (apps == null || apps.isEmpty()) {
                return;
            }

            // Update icon cache. This happens in segments and {@link #onPackageIconsUpdated}
            // is called by the icon cache when the job is complete.
            updateDBIcons(user, apps, UserHandleCompat.myUserHandle().equals(user)
                    ? ignorePackagesForMainUser : Collections.<String>emptySet());
        }
    }

    /**
     * Updates the persistent DB, such that only entries corresponding to {@param apps} remain in
     * the DB and are updated.
     *
     * @return The set of packages for which icons have updated.
     */
    private void updateDBIcons(UserHandleCompat user, List<LauncherActivityInfoCompat> apps,
                               Set<String> ignorePackages) {
        long userSerial = mUserManager.getSerialNumberForUser(user);
        PackageManager pm = mContext.getPackageManager();
        HashMap<String, PackageInfo> pkgInfoMap = new HashMap<String, PackageInfo>();
        for (PackageInfo info : pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)) {
            pkgInfoMap.put(info.packageName, info);
        }

        HashMap<ComponentName, LauncherActivityInfoCompat> componentMap = new HashMap<>();
        for (LauncherActivityInfoCompat app : apps) {
            componentMap.put(app.getComponentName(), app);
        }

        Cursor c = mIconDb.getReadableDatabase().query(IconDB.TABLE_NAME,
                new String[]{IconDB.COLUMN_ROWID, IconDB.COLUMN_COMPONENT,
                        IconDB.COLUMN_LAST_UPDATED, IconDB.COLUMN_VERSION,
                        IconDB.COLUMN_SYSTEM_STATE},
                IconDB.COLUMN_USER + " = ? ",
                new String[]{Long.toString(userSerial)},
                null, null, null);

        final int indexComponent = c.getColumnIndex(IconDB.COLUMN_COMPONENT);
        final int indexLastUpdate = c.getColumnIndex(IconDB.COLUMN_LAST_UPDATED);
        final int indexVersion = c.getColumnIndex(IconDB.COLUMN_VERSION);
        final int rowIndex = c.getColumnIndex(IconDB.COLUMN_ROWID);
        final int systemStateIndex = c.getColumnIndex(IconDB.COLUMN_SYSTEM_STATE);

        HashSet<Integer> itemsToRemove = new HashSet<Integer>();
        Stack<LauncherActivityInfoCompat> appsToUpdate = new Stack<>();

        while (c.moveToNext()) {
            String cn = c.getString(indexComponent);
            ComponentName component = ComponentName.unflattenFromString(cn);
            PackageInfo info = pkgInfoMap.get(component.getPackageName());
            if (info == null) {
                if (!ignorePackages.contains(component.getPackageName())) {
                    remove(component, user);
                    itemsToRemove.add(c.getInt(rowIndex));
                }
                continue;
            }
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_IS_DATA_ONLY) != 0) {
                // Application is not present
                continue;
            }

            long updateTime = c.getLong(indexLastUpdate);
            int version = c.getInt(indexVersion);
            LauncherActivityInfoCompat app = componentMap.remove(component);
            if (version == info.versionCode && updateTime == info.lastUpdateTime &&
                    TextUtils.equals(mSystemState, c.getString(systemStateIndex))) {
                continue;
            }
            if (app == null) {
                remove(component, user);
                itemsToRemove.add(c.getInt(rowIndex));
            } else {
                appsToUpdate.add(app);
            }
        }
        c.close();
        if (!itemsToRemove.isEmpty()) {
            mIconDb.getWritableDatabase().delete(IconDB.TABLE_NAME,
                    Utilities.createDbSelectionQuery(IconDB.COLUMN_ROWID, itemsToRemove),
                    null);
        }

        // Insert remaining apps.
        if (!componentMap.isEmpty() || !appsToUpdate.isEmpty()) {
            Stack<LauncherActivityInfoCompat> appsToAdd = new Stack<>();
            appsToAdd.addAll(componentMap.values());
            new SerializedIconUpdateTask(userSerial, pkgInfoMap,
                    appsToAdd, appsToUpdate).scheduleNext();
        }
    }

    @Thunk
    void addIconToDBAndMemCache(LauncherActivityInfoCompat app, PackageInfo info,
                                long userSerial) {
        // Reuse the existing entry if it already exists in the DB. This ensures that we do not
        // create bitmap if it was already created during loader.
        ContentValues values = updateCacheAndGetContentValues(app, false);
        addIconToDB(values, app.getComponentName(), info, userSerial);
    }

    /**
     * Updates {@param values} to contain versoning information and adds it to the DB.
     *
     * @param values {@link ContentValues} containing icon & title
     */
    private void addIconToDB(ContentValues values, ComponentName key,
                             PackageInfo info, long userSerial) {
        values.put(IconDB.COLUMN_COMPONENT, key.flattenToString());
        values.put(IconDB.COLUMN_USER, userSerial);
        values.put(IconDB.COLUMN_LAST_UPDATED, info.lastUpdateTime);
        values.put(IconDB.COLUMN_VERSION, info.versionCode);
        mIconDb.getWritableDatabase().insertWithOnConflict(IconDB.TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Thunk
    ContentValues updateCacheAndGetContentValues(LauncherActivityInfoCompat app,
                                                 boolean replaceExisting) {
        final ComponentKey key = new ComponentKey(app.getComponentName(), app.getUser());
        CacheEntry entry = null;
        if (!replaceExisting) {
            entry = mCache.get(key);
            // We can't reuse the entry if the high-res icon is not present.
            if (entry == null || entry.isLowResIcon || entry.icon == null) {
                entry = null;
            }
        }
        if (entry == null) {
            entry = new CacheEntry();
            entry.icon = Utilities.createIconBitmap(app.getBadgedIcon(mIconDpi), mContext);
        }
        entry.title = app.getLabel();
        entry.contentDescription = mUserManager.getBadgedLabelForUser(entry.title, app.getUser());
        mCache.put(new ComponentKey(app.getComponentName(), app.getUser()), entry);

        return newContentValues(entry.icon, entry.title.toString(), mActivityBgColor);
    }

    /**
     * Fetches high-res icon for the provided ItemInfo and updates the caller when done.
     *
     * @return a request ID that can be used to cancel the request.
     */
    public IconLoadRequest updateIconInBackground(final BubbleTextView caller, final ItemInfo info) {
        Runnable request = new Runnable() {

            @Override
            public void run() {
                if (info instanceof AppInfo) {
                    getTitleAndIcon((AppInfo) info, null, false);
                } else if (info instanceof ShortcutInfo) {
                    ShortcutInfo st = (ShortcutInfo) info;
                    getTitleAndIcon(st,
                            st.promisedIntent != null ? st.promisedIntent : st.intent,
                            st.user, false);
                } else if (info instanceof PackageItemInfo) {
                    PackageItemInfo pti = (PackageItemInfo) info;
                    getTitleAndIconForApp(pti.packageName, pti.user, false, pti);
                }
                mMainThreadExecutor.execute(new Runnable() {

                    @Override
                    public void run() {
                        caller.reapplyItemInfo(info);
                    }
                });
            }
        };
        mWorkerHandler.post(request);
        return new IconLoadRequest(request, mWorkerHandler);
    }

    private Bitmap getNonNullIcon(CacheEntry entry, UserHandleCompat user) {
        return entry.icon == null ? getDefaultIcon(user) : entry.icon;
    }

    /**
     * Fill in "application" with the icon and label for "info."
     */
    public synchronized void getTitleAndIcon(AppInfo application,
                                             LauncherActivityInfoCompat info, boolean useLowResIcon) {
        UserHandleCompat user = info == null ? application.user : info.getUser();
        CacheEntry entry = cacheLocked(application.componentName, info, user,
                false, useLowResIcon);
        application.title = Utilities.trim(entry.title);
        application.iconBitmap = getNonNullIcon(entry, user);
        application.contentDescription = entry.contentDescription;
        application.usingLowResIcon = entry.isLowResIcon;
    }

    /**
     * Updates {@param application} only if a valid entry is found.
     */
    public synchronized void updateTitleAndIcon(AppInfo application) {
        CacheEntry entry = cacheLocked(application.componentName, null, application.user,
                false, application.usingLowResIcon);
        if (entry.icon != null && !isDefaultIcon(entry.icon, application.user)) {
            application.title = Utilities.trim(entry.title);
            application.iconBitmap = entry.icon;
            application.contentDescription = entry.contentDescription;
            application.usingLowResIcon = entry.isLowResIcon;
        }
    }

    /**
     * Returns a high res icon for the given intent and user
     */
    public synchronized Bitmap getIcon(Intent intent, UserHandleCompat user) {
        ComponentName component = intent.getComponent();
        // null info means not installed, but if we have a component from the intent then
        // we should still look in the cache for restored app icons.
        if (component == null) {
            return getDefaultIcon(user);
        }

        LauncherActivityInfoCompat launcherActInfo = mLauncherApps.resolveActivity(intent, user);
        CacheEntry entry = cacheLocked(component, launcherActInfo, user, true, false /* useLowRes */);
        return entry.icon;
    }

    /**
     * Fill in {@param shortcutInfo} with the icon and label for {@param intent}. If the
     * corresponding activity is not found, it reverts to the package icon.
     */
    public synchronized void getTitleAndIcon(ShortcutInfo shortcutInfo, Intent intent,
                                             UserHandleCompat user, boolean useLowResIcon) {
        ComponentName component = intent.getComponent();
        // null info means not installed, but if we have a component from the intent then
        // we should still look in the cache for restored app icons.
        if (component == null) {
            shortcutInfo.setIcon(getDefaultIcon(user));
            shortcutInfo.title = "";
            shortcutInfo.usingFallbackIcon = true;
            shortcutInfo.usingLowResIcon = false;
        } else {
            LauncherActivityInfoCompat info = mLauncherApps.resolveActivity(intent, user);
            getTitleAndIcon(shortcutInfo, component, info, user, true, useLowResIcon);
        }
    }

    /**
     * Fill in {@param shortcutInfo} with the icon and label for {@param info}
     */
    public synchronized void getTitleAndIcon(
            ShortcutInfo shortcutInfo, ComponentName component, LauncherActivityInfoCompat info,
            UserHandleCompat user, boolean usePkgIcon, boolean useLowResIcon) {
        CacheEntry entry = cacheLocked(component, info, user, usePkgIcon, useLowResIcon);
        shortcutInfo.setIcon(getNonNullIcon(entry, user));
        shortcutInfo.title = Utilities.trim(entry.title);
        shortcutInfo.usingFallbackIcon = isDefaultIcon(entry.icon, user);
        shortcutInfo.usingLowResIcon = entry.isLowResIcon;
    }

    /**
     * Fill in {@param appInfo} with the icon and label for {@param packageName}
     */
    public synchronized void getTitleAndIconForApp(
            String packageName, UserHandleCompat user, boolean useLowResIcon,
            PackageItemInfo infoOut) {
        CacheEntry entry = getEntryForPackageLocked(packageName, user, useLowResIcon);
        infoOut.iconBitmap = getNonNullIcon(entry, user);
        infoOut.title = Utilities.trim(entry.title);
        infoOut.usingLowResIcon = entry.isLowResIcon;
        infoOut.contentDescription = entry.contentDescription;
    }

    public synchronized Bitmap getDefaultIcon(UserHandleCompat user) {
        if (!mDefaultIcons.containsKey(user)) {
            mDefaultIcons.put(user, makeDefaultIcon(user));
        }
        return mDefaultIcons.get(user);
    }

    public boolean isDefaultIcon(Bitmap icon, UserHandleCompat user) {
        return mDefaultIcons.get(user) == icon;
    }

    /**
     * Retrieves the entry from the cache. If the entry is not present, it creates a new entry.
     * This method is not thread safe, it must be called from a synchronized method.
     */
    private CacheEntry cacheLocked(ComponentName componentName, LauncherActivityInfoCompat info,
                                   UserHandleCompat user, boolean usePackageIcon, boolean useLowResIcon) {
        ComponentKey cacheKey = new ComponentKey(componentName, user);
        CacheEntry entry = mCache.get(cacheKey);
        if (entry == null || (entry.isLowResIcon && !useLowResIcon)) {
            entry = new CacheEntry();
            mCache.put(cacheKey, entry);

            // Check the DB first.
            if (!getEntryFromDB(cacheKey, entry, useLowResIcon)) {
                if (info != null) {
//                    entry.icon = Utilities.createIconBitmap(info.getBadgedIcon(mIconDpi), mContext);
                    //定制图标（需求觉得旧的系统应用图标太丑，可以通过这个类LauncherIconTheme来设置）
                    Drawable themeDrawable = LauncherIconTheme
                            .getIconDrawable(mContext,
                                    componentName.getPackageName(), componentName.getClassName());
                    if (themeDrawable != null) {
                        entry.icon = Utilities.createIconBitmap(
                                themeDrawable, mContext);
                    } else {
                        entry.icon = Utilities.createIconBitmap(
                                info.getBadgedIcon(mIconDpi), mContext);
                    }
                } else {
                    if (usePackageIcon) {
                        CacheEntry packageEntry = getEntryForPackageLocked(
                                componentName.getPackageName(), user, false);
                        if (packageEntry != null) {
                            if (DEBUG) Log.d(TAG, "using package default icon for " +
                                    componentName.toShortString());
                            entry.icon = packageEntry.icon;
                            entry.title = packageEntry.title;
                            entry.contentDescription = packageEntry.contentDescription;
                        }
                    }
                    if (entry.icon == null) {
                        if (DEBUG) Log.d(TAG, "using default icon for " +
                                componentName.toShortString());
                        entry.icon = getDefaultIcon(user);
                    }
                }
            }

            if (TextUtils.isEmpty(entry.title) && info != null) {
                entry.title = info.getLabel();
                entry.contentDescription = mUserManager.getBadgedLabelForUser(entry.title, user);
            }
        }

        boolean containCurrentApp = pkgAppDetailsMap.containsKey(componentName.getPackageName());

        // Android Launcher图标定制 https://www.jianshu.com/p/a601c54e4c5d
        // 图标圆角处理 https://blog.csdn.net/l448288137/article/details/48276681
        if (mEnableIconRoundCorner) {
            int width = entry.icon.getWidth();
            int height = entry.icon.getHeight();
            Bitmap newIcon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newIcon);
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            //画出4个圆角
            final RectF rectF = new RectF(0, 0, width, height);
            float roundPx = mIconRoundCornerSize;
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            // 绘制图标
            Rect src = new Rect(0, 0, width, height);
            if (mEnableIconReplace && containCurrentApp) { // 本地有自定义图标
                DesktopIconInfo.AppsdetailsBean currentAppInfo = pkgAppDetailsMap.get(componentName.getPackageName());
                String pngPath = currentAppInfo.icon;
                if (!TextUtils.isEmpty(pngPath) && FileUtils.isFileExists(pngPath)) {
                    setScale(pngPath);
                    canvas.drawBitmap(mEnableIconBackgroundColor ? drawBg4Bitmap(mIconBackgroundColor, BitmapFactory.decodeFile(pngPath)) : BitmapFactory.decodeFile(pngPath), null, src, paint);
                } else {
                    canvas.drawBitmap(mEnableIconBackgroundColor ? drawBg4Bitmap(mIconBackgroundColor, entry.icon) : entry.icon, src, src, paint);
                }
                if (!TextUtils.isEmpty(currentAppInfo.appname)) {
                    entry.title = currentAppInfo.appname;
                }
            } else {
                canvas.drawBitmap(mEnableIconBackgroundColor ? drawBg4Bitmap(mIconBackgroundColor, entry.icon) : entry.icon, src, src, paint);
            }
            canvas.save();
            canvas.restore();
            entry.icon = newIcon;
        } else if (mEnableIconReplace && containCurrentApp) {
            int width = entry.icon.getWidth();
            int height = entry.icon.getHeight();
            Bitmap newIcon = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newIcon);
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            // 绘制图标
            Rect src = new Rect(0, 0, width, height);
            DesktopIconInfo.AppsdetailsBean currentAppInfo = pkgAppDetailsMap.get(componentName.getPackageName());
            String pngPath = currentAppInfo.icon;
            if (!TextUtils.isEmpty(pngPath) && FileUtils.isFileExists(pngPath)) {
                setScale(pngPath);
                canvas.drawBitmap(mEnableIconBackgroundColor ? drawBg4Bitmap(mIconBackgroundColor, BitmapFactory.decodeFile(pngPath)) : BitmapFactory.decodeFile(pngPath), null, src, paint);
            } else {
                canvas.drawBitmap(mEnableIconBackgroundColor ? drawBg4Bitmap(mIconBackgroundColor, entry.icon) : entry.icon, src, src, paint);
            }
            if (!TextUtils.isEmpty(currentAppInfo.appname)) {
                entry.title = currentAppInfo.appname;
            }
            canvas.save();
            canvas.restore();
            entry.icon = newIcon;
        }

        return entry;
    }

    /**
     * 计算桌面图标长宽比例
     * @param pngPath
     */
    private void setScale(String pngPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pngPath, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        mIconScale = Math.max(outWidth / (float) outHeight, mIconScale);
    }

    public float getIconScale() {
        return mIconScale;
    }

    /**
     * 绘制白色背景图标
     * @param color
     * @param originBitmap
     * @return
     */
    public static Bitmap drawBg4Bitmap(int color, Bitmap originBitmap) {
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(originBitmap.getWidth(),
                originBitmap.getHeight(), originBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, originBitmap.getWidth(), originBitmap.getHeight(), paint);
        canvas.drawBitmap(originBitmap, 0, 0, paint);
        return bitmap;
    }

    /**
     * Adds a default package entry in the cache. This entry is not persisted and will be removed
     * when the cache is flushed.
     */
    public synchronized void cachePackageInstallInfo(String packageName, UserHandleCompat user,
                                                     Bitmap icon, CharSequence title) {
        removeFromMemCacheLocked(packageName, user);

        ComponentKey cacheKey = getPackageKey(packageName, user);
        CacheEntry entry = mCache.get(cacheKey);

        // For icon caching, do not go through DB. Just update the in-memory entry.
        if (entry == null) {
            entry = new CacheEntry();
            mCache.put(cacheKey, entry);
        }
        if (!TextUtils.isEmpty(title)) {
            entry.title = title;
        }
        if (icon != null) {
            entry.icon = Utilities.createIconBitmap(icon, mContext);
        }
    }

    private static ComponentKey getPackageKey(String packageName, UserHandleCompat user) {
        ComponentName cn = new ComponentName(packageName, packageName + EMPTY_CLASS_NAME);
        return new ComponentKey(cn, user);
    }

    /**
     * Gets an entry for the package, which can be used as a fallback entry for various components.
     * This method is not thread safe, it must be called from a synchronized method.
     */
    private CacheEntry getEntryForPackageLocked(String packageName, UserHandleCompat user,
                                                boolean useLowResIcon) {
        ComponentKey cacheKey = getPackageKey(packageName, user);
        CacheEntry entry = mCache.get(cacheKey);

        if (entry == null || (entry.isLowResIcon && !useLowResIcon)) {
            entry = new CacheEntry();
            boolean entryUpdated = true;

            // Check the DB first.
            if (!getEntryFromDB(cacheKey, entry, useLowResIcon)) {
                try {
                    int flags = UserHandleCompat.myUserHandle().equals(user) ? 0 :
                            PackageManager.GET_UNINSTALLED_PACKAGES;
                    PackageInfo info = mPackageManager.getPackageInfo(packageName, flags);
                    ApplicationInfo appInfo = info.applicationInfo;
                    if (appInfo == null) {
                        throw new NameNotFoundException("ApplicationInfo is null");
                    }
                    Drawable drawable = mUserManager.getBadgedDrawableForUser(
                            appInfo.loadIcon(mPackageManager), user);
                    entry.icon = Utilities.createIconBitmap(drawable, mContext);
                    entry.title = appInfo.loadLabel(mPackageManager);
                    entry.contentDescription = mUserManager.getBadgedLabelForUser(entry.title, user);
                    entry.isLowResIcon = false;

                    // Add the icon in the DB here, since these do not get written during
                    // package updates.
                    ContentValues values =
                            newContentValues(entry.icon, entry.title.toString(), mPackageBgColor);
                    addIconToDB(values, cacheKey.componentName, info,
                            mUserManager.getSerialNumberForUser(user));

                } catch (NameNotFoundException e) {
                    if (DEBUG) Log.d(TAG, "Application not installed " + packageName);
                    entryUpdated = false;
                }
            }

            // Only add a filled-out entry to the cache
            if (entryUpdated) {
                mCache.put(cacheKey, entry);
            }
        }
        return entry;
    }

    /**
     * Pre-load an icon into the persistent cache.
     *
     * <P>Queries for a component that does not exist in the package manager
     * will be answered by the persistent cache.
     *
     * @param componentName the icon should be returned for this component
     * @param icon          the icon to be persisted
     * @param dpi           the native density of the icon
     */
    public void preloadIcon(ComponentName componentName, Bitmap icon, int dpi, String label,
                            long userSerial, InvariantDeviceProfile idp) {
        // TODO rescale to the correct native DPI
        try {
            PackageManager packageManager = mContext.getPackageManager();
            packageManager.getActivityIcon(componentName);
            // component is present on the system already, do nothing
            return;
        } catch (PackageManager.NameNotFoundException e) {
            // pass
        }

        ContentValues values = newContentValues(
                Bitmap.createScaledBitmap(icon, idp.iconBitmapSize, idp.iconBitmapSize, true),
                label, Color.TRANSPARENT);
        values.put(IconDB.COLUMN_COMPONENT, componentName.flattenToString());
        values.put(IconDB.COLUMN_USER, userSerial);
        mIconDb.getWritableDatabase().insertWithOnConflict(IconDB.TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    private boolean getEntryFromDB(ComponentKey cacheKey, CacheEntry entry, boolean lowRes) {
        Cursor c = mIconDb.getReadableDatabase().query(IconDB.TABLE_NAME,
                new String[]{lowRes ? IconDB.COLUMN_ICON_LOW_RES : IconDB.COLUMN_ICON,
                        IconDB.COLUMN_LABEL},
                IconDB.COLUMN_COMPONENT + " = ? AND " + IconDB.COLUMN_USER + " = ?",
                new String[]{cacheKey.componentName.flattenToString(),
                        Long.toString(mUserManager.getSerialNumberForUser(cacheKey.user))},
                null, null, null);
        try {
            if (c.moveToNext()) {
                entry.icon = loadIconNoResize(c, 0, lowRes ? mLowResOptions : null);
                entry.isLowResIcon = lowRes;
                entry.title = c.getString(1);
                if (entry.title == null) {
                    entry.title = "";
                    entry.contentDescription = "";
                } else {
                    entry.contentDescription = mUserManager.getBadgedLabelForUser(
                            entry.title, cacheKey.user);
                }
                return true;
            }
        } finally {
            c.close();
        }
        return false;
    }

    public static class IconLoadRequest {
        private final Runnable mRunnable;
        private final Handler mHandler;

        IconLoadRequest(Runnable runnable, Handler handler) {
            mRunnable = runnable;
            mHandler = handler;
        }

        public void cancel() {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /**
     * A runnable that updates invalid icons and adds missing icons in the DB for the provided
     * LauncherActivityInfoCompat list. Items are updated/added one at a time, so that the
     * worker thread doesn't get blocked.
     */
    @Thunk
    class SerializedIconUpdateTask implements Runnable {
        private final long mUserSerial;
        private final HashMap<String, PackageInfo> mPkgInfoMap;
        private final Stack<LauncherActivityInfoCompat> mAppsToAdd;
        private final Stack<LauncherActivityInfoCompat> mAppsToUpdate;
        private final HashSet<String> mUpdatedPackages = new HashSet<String>();

        @Thunk
        SerializedIconUpdateTask(long userSerial, HashMap<String, PackageInfo> pkgInfoMap,
                                 Stack<LauncherActivityInfoCompat> appsToAdd,
                                 Stack<LauncherActivityInfoCompat> appsToUpdate) {
            mUserSerial = userSerial;
            mPkgInfoMap = pkgInfoMap;
            mAppsToAdd = appsToAdd;
            mAppsToUpdate = appsToUpdate;
        }

        @Override
        public void run() {
            if (!mAppsToUpdate.isEmpty()) {
                LauncherActivityInfoCompat app = mAppsToUpdate.pop();
                String cn = app.getComponentName().flattenToString();
                ContentValues values = updateCacheAndGetContentValues(app, true);
                mIconDb.getWritableDatabase().update(IconDB.TABLE_NAME, values,
                        IconDB.COLUMN_COMPONENT + " = ? AND " + IconDB.COLUMN_USER + " = ?",
                        new String[]{cn, Long.toString(mUserSerial)});
                mUpdatedPackages.add(app.getComponentName().getPackageName());

                if (mAppsToUpdate.isEmpty() && !mUpdatedPackages.isEmpty()) {
                    // No more app to update. Notify model.
                    LauncherAppState.getInstance().getModel().onPackageIconsUpdated(
                            mUpdatedPackages, mUserManager.getUserForSerialNumber(mUserSerial));
                }

                // Let it run one more time.
                scheduleNext();
            } else if (!mAppsToAdd.isEmpty()) {
                LauncherActivityInfoCompat app = mAppsToAdd.pop();
                PackageInfo info = mPkgInfoMap.get(app.getComponentName().getPackageName());
                if (info != null) {
                    synchronized (IconCache.this) {
                        addIconToDBAndMemCache(app, info, mUserSerial);
                    }
                }

                if (!mAppsToAdd.isEmpty()) {
                    scheduleNext();
                }
            }
        }

        public void scheduleNext() {
            mWorkerHandler.postAtTime(this, ICON_UPDATE_TOKEN, SystemClock.uptimeMillis() + 1);
        }
    }

    private void updateSystemStateString() {
        mSystemState = Locale.getDefault().toString();
    }

    private static final class IconDB extends SQLiteOpenHelper {
        private final static int DB_VERSION = 7;

        private final static String TABLE_NAME = "icons";
        private final static String COLUMN_ROWID = "rowid";
        private final static String COLUMN_COMPONENT = "componentName";
        private final static String COLUMN_USER = "profileId";
        private final static String COLUMN_LAST_UPDATED = "lastUpdated";
        private final static String COLUMN_VERSION = "version";
        private final static String COLUMN_ICON = "icon";
        private final static String COLUMN_ICON_LOW_RES = "icon_low_res";
        private final static String COLUMN_LABEL = "label";
        private final static String COLUMN_SYSTEM_STATE = "system_state";

        public IconDB(Context context) {
            super(
                    LocalDBContext.Factory.newAppIconsContext(context),
                    LauncherFiles.APP_ICONS_DB, null, DB_VERSION
            );
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_COMPONENT + " TEXT NOT NULL, " +
                    COLUMN_USER + " INTEGER NOT NULL, " +
                    COLUMN_LAST_UPDATED + " INTEGER NOT NULL DEFAULT 0, " +
                    COLUMN_VERSION + " INTEGER NOT NULL DEFAULT 0, " +
                    COLUMN_ICON + " BLOB, " +
                    COLUMN_ICON_LOW_RES + " BLOB, " +
                    COLUMN_LABEL + " TEXT, " +
                    COLUMN_SYSTEM_STATE + " TEXT, " +
                    "PRIMARY KEY (" + COLUMN_COMPONENT + ", " + COLUMN_USER + ") " +
                    ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                clearDB(db);
            }
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                clearDB(db);
            }
        }

        private void clearDB(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    private ContentValues newContentValues(Bitmap icon, String label, int lowResBackgroundColor) {
        ContentValues values = new ContentValues();
        values.put(IconDB.COLUMN_ICON, Utilities.flattenBitmap(icon));

        values.put(IconDB.COLUMN_LABEL, label);
        values.put(IconDB.COLUMN_SYSTEM_STATE, mSystemState);

        if (lowResBackgroundColor == Color.TRANSPARENT) {
            values.put(IconDB.COLUMN_ICON_LOW_RES, Utilities.flattenBitmap(
                    Bitmap.createScaledBitmap(icon,
                            icon.getWidth() / LOW_RES_SCALE_FACTOR,
                            icon.getHeight() / LOW_RES_SCALE_FACTOR, true)));
        } else {
            synchronized (this) {
                if (mLowResBitmap == null) {
                    mLowResBitmap = Bitmap.createBitmap(icon.getWidth() / LOW_RES_SCALE_FACTOR,
                            icon.getHeight() / LOW_RES_SCALE_FACTOR, Bitmap.Config.RGB_565);
                    mLowResCanvas = new Canvas(mLowResBitmap);
                    mLowResPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
                }
                mLowResCanvas.drawColor(lowResBackgroundColor);
                mLowResCanvas.drawBitmap(icon, new Rect(0, 0, icon.getWidth(), icon.getHeight()),
                        new Rect(0, 0, mLowResBitmap.getWidth(), mLowResBitmap.getHeight()),
                        mLowResPaint);
                values.put(IconDB.COLUMN_ICON_LOW_RES, Utilities.flattenBitmap(mLowResBitmap));
            }
        }
        return values;
    }

    private static Bitmap loadIconNoResize(Cursor c, int iconIndex, BitmapFactory.Options options) {
        byte[] data = c.getBlob(iconIndex);
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Exception e) {
            return null;
        }
    }
}
