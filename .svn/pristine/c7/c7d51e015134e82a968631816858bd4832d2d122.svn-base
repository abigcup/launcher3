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

import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Parcelable;
import android.os.Process;
import android.os.SystemClock;
import android.os.TransactionTooLargeException;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pair;
import android.view.View;

import com.android.Utils.ShellUtils2;
import com.android.hwyun.batchinstall.bean.DownloadFileInfo;
import com.android.hwyun.batchinstall.bean.ShortDownloadFileInfo;
import com.android.hwyun.batchinstall.hwcloud.DownloadManager;
import com.android.hwyun.batchinstall.hwcloud.OnDownloadListener;
import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.common.util.AppUtil;
import com.android.hwyun.prevshortcut.PrevShortcutContract;
import com.android.hwyun.prevshortcut.bean.PresetShortcutAction;
import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;
import com.android.hwyun.prevshortcut.presenter.PrevAppsShortcutPresent;
import com.android.hwyun.statistics.SaveAppDataModel;
import com.android.launcher3.arrange.ArrangeBean;
import com.android.launcher3.bean.ChannelIconInfo;
import com.android.launcher3.bean.HotSeatInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherActivityInfoCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.compat.PackageInstallerCompat.PackageInstallInfo;
import com.android.launcher3.compat.UserHandleCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.FeaturesConfig;
import com.android.launcher3.model.MigrateFromRestoreTask;
import com.android.launcher3.model.WidgetsModel;
import com.android.launcher3.bean.PresetShortData;
import com.android.launcher3.util.ArrangeUtils;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.CursorIconInfo;
import com.android.launcher3.util.HotSeatMgr;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.ManagedProfileHeuristic;
import com.android.launcher3.util.SettingConfig;
import com.android.launcher3.util.Thunk;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.annotations.Nullable;


/**
 * Maintains in-memory state of the Launcher. It is expected that there should be only one
 * LauncherModel object held in a static. Also provide APIs for updating the database state
 * for the Launcher.
 */
public class LauncherModel extends BroadcastReceiver
        implements LauncherAppsCompat.OnAppsChangedCallbackCompat, PrevShortcutContract.DetailPresent, OnDownloadListener {
    static final boolean DEBUG_LOADERS = true;
    private static final boolean DEBUG_RECEIVER = false;
    private static final boolean REMOVE_UNRESTORED_ICONS = true;

    static final String TAG = "Launcher.Model";

    public static final int LOADER_FLAG_NONE = 0;
    public static final int LOADER_FLAG_CLEAR_WORKSPACE = 1 << 0;
    public static final int LOADER_FLAG_MIGRATE_SHORTCUTS = 1 << 1;

    private static final int ITEMS_CHUNK = 6; // batch size for the workspace icons
    private static final long INVALID_SCREEN_ID = -1L;

    @Thunk
    final boolean mAppsCanBeOnRemoveableStorage;
    private final boolean mOldContentProviderExists;

    @Thunk
    final LauncherAppState mApp;
    //保护的是LoaderTask的状态
    @Thunk
    final Object mLock = new Object();
    @Thunk
    DeferredHandler mHandler = new DeferredHandler();
    @Thunk
    LoaderTask mLoaderTask;
    @Thunk
    boolean mIsLoaderTaskRunning;
    @Thunk
    boolean mHasLoaderCompletedOnce;

    private static final String MIGRATE_AUTHORITY = "com.android.launcher2.settings";

    @Thunk
    static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");

    static {
        sWorkerThread.start();
    }

    @Thunk
    static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    // We start off with everything not loaded.  After that, we assume that
    // our monitoring of the package manager provides all updates and we never
    // need to do a requery.  These are only ever touched from the loader thread.
    @Thunk
    boolean mWorkspaceLoaded;
    @Thunk
    boolean mAllAppsLoaded;

    // When we are loading pages synchronously, we can't just post the binding of items on the side
    // pages as this delays the rotation process.  Instead, we wait for a callback from the first
    // draw (in Workspace) to initiate the binding of the remaining side pages.  Any time we start
    // a normal load, we also clear this set of Runnables.
    static final ArrayList<Runnable> mDeferredBindRunnables = new ArrayList<Runnable>();

    /**
     * Set of runnables to be called on the background thread after the workspace binding
     * is complete.
     */
    static final ArrayList<Runnable> mBindCompleteRunnables = new ArrayList<Runnable>();

    @Thunk
    WeakReference<Callbacks> mCallbacks;

    // < only access in worker thread >
    AllAppsList mBgAllAppsList;
    // Entire list of widgets.
    WidgetsModel mBgWidgetsModel;

    // The lock that must be acquired before referencing any static bg data structures.  Unlike
    // other locks, this one can generally be held long-term because we never expect any of these
    // static data structures to be referenced outside of the worker thread except on the first
    // load after configuration change.
    static final Object sBgLock = new Object();

    //存储数据库获取到的预置图标信息（后面可以和当前这次获取的预置图标信息比对过滤）
    static final ArrayList<ShortcutInfo> sPresetItemsIdList = new ArrayList<>();

    // sBgItemsIdMap maps *all* the ItemInfos (shortcuts, folders, and widgets) created by
    // LauncherModel to their ids
    static final LongArrayMap<ItemInfo> sBgItemsIdMap = new LongArrayMap<>();

    // sBgWorkspaceItems is passed to bindItems, which expects a list of all folders and shortcuts
    //       created by LauncherModel that are directly on the home screen (however, no widgets or
    //       shortcuts within folders).
    static final ArrayList<ItemInfo> sBgWorkspaceItems = new ArrayList<ItemInfo>();

    // sBgAppWidgets is all LauncherAppWidgetInfo created by LauncherModel. Passed to bindAppWidget()
    static final ArrayList<LauncherAppWidgetInfo> sBgAppWidgets =
            new ArrayList<LauncherAppWidgetInfo>();

    // sBgFolders is all FolderInfos created by LauncherModel. Passed to bindFolders()
    static final LongArrayMap<FolderInfo> sBgFolders = new LongArrayMap<>();

    // sBgWorkspaceScreens is the ordered set of workspace screens.
    static final ArrayList<Long> sBgWorkspaceScreens = new ArrayList<Long>();

    // sBgWidgetProviders is the set of widget providers including custom internal widgets
    public static HashMap<ComponentKey, LauncherAppWidgetProviderInfo> sBgWidgetProviders;

    // sPendingPackages is a set of packages which could be on sdcard and are not available yet
    static final HashMap<UserHandleCompat, HashSet<String>> sPendingPackages =
            new HashMap<UserHandleCompat, HashSet<String>>();

    // </ only access in worker thread >

    //记录文件夹位置
    static HashMap<String, Pair<Long, int[]>> sFolderLocalMap = new HashMap<>();

    @Thunk
    IconCache mIconCache;

    @Thunk
    final LauncherAppsCompat mLauncherApps;
    @Thunk
    final UserManagerCompat mUserManager;

    //记录当前的忽略显示桌面应用列表：包名A|包名B|...
    private static String sIgnoreShowAppsList = null;
    private static String sIgnoreShortcutAppsList = null;

    //下载快捷方式相关
    private Map<Long, ResponeAppsShortcut> downloadingFengWo = new HashMap<>();
    private Map<Long/*Channle*/, View/*shortcut view*/> downloadItemViews = new HashMap<>();

    private SaveAppDataModel saveAppDataModel = new SaveAppDataModel();

    private List<Long> mockTimer = new ArrayList<>();

    public interface Callbacks {
        public boolean setLoadOnResume();

        public int getCurrentWorkspaceScreen();

        public void startBinding();

        public void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end,
                              boolean forceAnimateIcons);

        public void bindScreens(ArrayList<Long> orderedScreenIds);

        public void bindAddScreens(ArrayList<Long> orderedScreenIds);

        public void bindFolders(LongArrayMap<FolderInfo> folders);

        public void finishBindingItems();

        public void bindAppWidget(LauncherAppWidgetInfo info);

        public void bindAllApplications(ArrayList<AppInfo> apps);

        public void bindAppsAdded(ArrayList<Long> newScreens,
                                  ArrayList<ItemInfo> addNotAnimated,
                                  ArrayList<ItemInfo> addAnimated,
                                  ArrayList<AppInfo> addedApps);

        public void bindAppsUpdated(ArrayList<AppInfo> apps);

        public void bindPresetShortcutsChanged(final ShortcutInfo updated);

        public void bindShortcutsChanged(ArrayList<ShortcutInfo> updated,
                                         ArrayList<ShortcutInfo> removed, UserHandleCompat user);

        public void bindWidgetsRestored(ArrayList<LauncherAppWidgetInfo> widgets);

        public void bindRestoreItemsChange(HashSet<ItemInfo> updates);

        public void bindComponentsRemoved(ArrayList<String> packageNames,
                                          ArrayList<AppInfo> appInfos, UserHandleCompat user, int reason);

        public void bindAllPackages(WidgetsModel model);

        public void bindSearchProviderChanged();

        public boolean isAllAppsButtonRank(int rank);

        public void onPageBoundSynchronously(int page);

        public void dumpLogsToLocalData();

        //只从桌面上删除图标，并没有更改数据库
        public void deleteDesktopShortcut(ShortcutInfo existItemInfo);

        //添加图标
        public void completeAddShortcut(String folderName, ShortcutInfo shortcutInfo);

        public void replaceFolderWithFinalItem(FolderInfo folderInfo);

    }

    public interface ItemInfoFilter {
        public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn);
    }

    LauncherModel(LauncherAppState app, IconCache iconCache, AppFilter appFilter) {
        Context context = app.getContext();

        mAppsCanBeOnRemoveableStorage = Environment.isExternalStorageRemovable();
        String oldProvider = context.getString(R.string.old_launcher_provider_uri);
        // This may be the same as MIGRATE_AUTHORITY, or it may be replaced by a different
        // resource string.
        String redirectAuthority = Uri.parse(oldProvider).getAuthority();
        ProviderInfo providerInfo =
                context.getPackageManager().resolveContentProvider(MIGRATE_AUTHORITY, 0);
        ProviderInfo redirectProvider =
                context.getPackageManager().resolveContentProvider(redirectAuthority, 0);

        Log.d(TAG, "Old launcher provider: " + oldProvider);
        mOldContentProviderExists = (providerInfo != null) && (redirectProvider != null);

        if (mOldContentProviderExists) {
            Log.d(TAG, "Old launcher provider exists.");
        } else {
            Log.d(TAG, "Old launcher provider does not exist.");
        }

        mApp = app;
        mBgAllAppsList = new AllAppsList(iconCache, appFilter);
        mBgWidgetsModel = new WidgetsModel(context, iconCache, appFilter);
        mIconCache = iconCache;

        mLauncherApps = LauncherAppsCompat.getInstance(context);
        mUserManager = UserManagerCompat.getInstance(context);
    }

    /**
     * Runs the specified runnable immediately if called from the main thread, otherwise it is
     * posted on the main thread handler.
     */
    @Thunk
    void runOnMainThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            // If we are on the worker thread, post onto the main handler
            mHandler.post(r);
        } else {
            r.run();
        }
    }

    /**
     * Runs the specified runnable immediately if called from the worker thread, otherwise it is
     * posted on the worker thread handler.
     */
    @Thunk
    static void runOnWorkerThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            // If we are not on the worker thread, then post to the worker handler
            sWorker.post(r);
        }
    }

    boolean canMigrateFromOldLauncherDb(Launcher launcher) {
        return mOldContentProviderExists && !launcher.isLauncherPreinstalled();
    }

    public void setPackageState(final PackageInstallInfo installInfo) {
        Runnable updateRunnable = new Runnable() {

            @Override
            public void run() {
                synchronized (sBgLock) {
                    final HashSet<ItemInfo> updates = new HashSet<>();

                    if (installInfo.state == PackageInstallerCompat.STATUS_INSTALLED) {
                        // Ignore install success events as they are handled by Package add events.
                        return;
                    }

                    for (ItemInfo info : sBgItemsIdMap) {
                        if (info instanceof ShortcutInfo) {
                            ShortcutInfo si = (ShortcutInfo) info;
                            ComponentName cn = si.getTargetComponent();
                            if (si.isPromise() && (cn != null)
                                    && installInfo.packageName.equals(cn.getPackageName())) {
                                si.setInstallProgress(installInfo.progress);

                                if (installInfo.state == PackageInstallerCompat.STATUS_FAILED) {
                                    // Mark this info as broken.
                                    si.status &= ~ShortcutInfo.FLAG_INSTALL_SESSION_ACTIVE;
                                }
                                updates.add(si);
                            }
                        }
                    }

                    for (LauncherAppWidgetInfo widget : sBgAppWidgets) {
                        if (widget.providerName.getPackageName().equals(installInfo.packageName)) {
                            widget.installProgress = installInfo.progress;
                            updates.add(widget);
                        }
                    }

                    if (!updates.isEmpty()) {
                        // Push changes to the callback.
                        Runnable r = new Runnable() {
                            public void run() {
                                Callbacks callbacks = getCallback();
                                if (callbacks != null) {
                                    callbacks.bindRestoreItemsChange(updates);
                                }
                            }
                        };
                        mHandler.post(r);
                    }
                }
            }
        };
        runOnWorkerThread(updateRunnable);
    }

    /**
     * Updates the icons and label of all pending icons for the provided package name.
     */
    public void updateSessionDisplayInfo(final String packageName) {
        Runnable updateRunnable = new Runnable() {

            @Override
            public void run() {
                synchronized (sBgLock) {
                    final ArrayList<ShortcutInfo> updates = new ArrayList<>();
                    final UserHandleCompat user = UserHandleCompat.myUserHandle();

                    for (ItemInfo info : sBgItemsIdMap) {
                        if (info instanceof ShortcutInfo) {
                            ShortcutInfo si = (ShortcutInfo) info;
                            ComponentName cn = si.getTargetComponent();
                            if (si.isPromise() && (cn != null)
                                    && packageName.equals(cn.getPackageName())) {
                                if (si.hasStatusFlag(ShortcutInfo.FLAG_AUTOINTALL_ICON)) {
                                    // For auto install apps update the icon as well as label.
                                    mIconCache.getTitleAndIcon(si,
                                            si.promisedIntent, user,
                                            si.shouldUseLowResIcon());
                                } else {
                                    // Only update the icon for restored apps.
                                    si.updateIcon(mIconCache);
                                }
                                updates.add(si);
                            }
                        }
                    }

                    if (!updates.isEmpty()) {
                        // Push changes to the callback.
                        Runnable r = new Runnable() {
                            public void run() {
                                Callbacks callbacks = getCallback();
                                if (callbacks != null) {
                                    callbacks.bindShortcutsChanged(updates,
                                            new ArrayList<ShortcutInfo>(), user);
                                }
                            }
                        };
                        mHandler.post(r);
                    }
                }
            }
        };
        runOnWorkerThread(updateRunnable);
    }

    public void addAppsToAllApps(final Context ctx, final ArrayList<AppInfo> allAppsApps) {
        final Callbacks callbacks = getCallback();

        if (allAppsApps == null) {
            throw new RuntimeException("allAppsApps must not be null");
        }
        if (allAppsApps.isEmpty()) {
            return;
        }

        // Process the newly added applications and add them to the database first
        Runnable r = new Runnable() {
            public void run() {
                runOnMainThread(new Runnable() {
                    public void run() {
                        Callbacks cb = getCallback();
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsAdded(null, null, null, allAppsApps);
                        }
                    }
                });
            }
        };
        runOnWorkerThread(r);
    }

    private static boolean findNextAvailableIconSpaceInScreen(ArrayList<ItemInfo> occupiedPos,
                                                              int[] xy, int spanX, int spanY) {
        LauncherAppState app = LauncherAppState.getInstance();
        InvariantDeviceProfile profile = app.getInvariantDeviceProfile();
        final int xCount = (int) profile.numColumns;
        final int yCount = (int) profile.numRows;
        boolean[][] occupied = new boolean[xCount][yCount];
        if (occupiedPos != null) {
            for (ItemInfo r : occupiedPos) {
                int right = r.cellX + r.spanX;
                int bottom = r.cellY + r.spanY;
                for (int x = r.cellX; 0 <= x && x < right && x < xCount; x++) {
                    for (int y = r.cellY; 0 <= y && y < bottom && y < yCount; y++) {
                        occupied[x][y] = true;
                    }
                }
            }
        }
        return Utilities.findVacantCell(xy, spanX, spanY, xCount, yCount, occupied);
    }

    /**
     * Find a position on the screen for the given size or adds a new screen.
     *
     * @return screenId and the coordinates for the item.
     */
    @Thunk
    Pair<Long, int[]> findSpaceForItem(
            Context context,
            ArrayList<Long> workspaceScreens,
            ArrayList<Long> addedWorkspaceScreensFinal,
            int spanX, int spanY) {
        LongSparseArray<ArrayList<ItemInfo>> screenItems = new LongSparseArray<>();

        // Use sBgItemsIdMap as all the items are already loaded.
        assertWorkspaceLoaded();

        // Find appropriate space for the item.
        long screenId = 0;
        int[] cordinates = new int[2];

        synchronized (sBgLock) {
            for (ItemInfo info : sBgItemsIdMap) {
                if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    ArrayList<ItemInfo> items = screenItems.get(info.screenId);
                    if (items == null) {
                        items = new ArrayList<>();
                        screenItems.put(info.screenId, items);
                    }
                    items.add(info);
                }
            }

            boolean found = false;

            int screenCount = workspaceScreens.size();
            // First check the preferred screen.
//        int preferredScreenIndex = workspaceScreens.isEmpty() ? 0 : 1;
            int preferredScreenIndex = 0;
            if (preferredScreenIndex < screenCount) {
                screenId = workspaceScreens.get(preferredScreenIndex);
                found = findNextAvailableIconSpaceInScreen(
                        screenItems.get(screenId), cordinates, spanX, spanY);
            }

            if (!found) {
                // Search on any of the screens starting from the first screen.
                for (int screen = 1; screen < screenCount; screen++) {
                    screenId = workspaceScreens.get(screen);
                    if (findNextAvailableIconSpaceInScreen(
                            screenItems.get(screenId), cordinates, spanX, spanY)) {
                        // We found a space for it
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                // Still no position found. Add a new screen to the end.
                screenId = LauncherAppState.getLauncherProvider().generateNewScreenId();

                // Save the screen id for binding in the workspace
                workspaceScreens.add(screenId);
                addedWorkspaceScreensFinal.add(screenId);

                // If we still can't find an empty space, then God help us all!!!
                if (!findNextAvailableIconSpaceInScreen(
                        screenItems.get(screenId), cordinates, spanX, spanY)) {
                    throw new RuntimeException("Can't find space to add the item");
                }
            }
        }
        return Pair.create(screenId, cordinates);
    }

    Pair<Long, int[]> findSpaceForItemIfNeedNewScreen(
            Context context,
            ArrayList<Long> workspaceScreens,
            ArrayList<Long> addedWorkspaceScreensFinal,
            int spanX, int spanY) {
        Pair<Long, int[]> spaceForItem = findSpaceForItem(context, workspaceScreens, addedWorkspaceScreensFinal, spanX, spanY);
        if (!addedWorkspaceScreensFinal.isEmpty()) {
            Launcher launcher = (Launcher) getCallback();
            launcher.getWorkspace().insertNewWorkspaceScreenBeforeEmptyScreen(spaceForItem.first);
            updateWorkspaceScreenOrderDirect(context, launcher.getWorkspace().mScreenOrder);
        }
        return spaceForItem;
    }


    public static boolean isIgnoreShowApp(String packageName) {
        //先判断是不是自己本身
        if (packageName.equals(AppUtils.getAppPackageName())) {
            LogUtils.d("cannot show this icon: " + packageName);
            return true;
        }

        if (sIgnoreShowAppsList == null) {
            sIgnoreShowAppsList = "";
            //解析xml配置

            try {
                XmlPullParser xmlResourceParser = SettingConfig.getInstance().getIgnoreShowApp();
                if (xmlResourceParser == null) {
                    xmlResourceParser = Utils.getApp().getResources().getXml(R.xml.ignore_show_apps);
                }
                int event = xmlResourceParser.getEventType();
                while (event != XmlResourceParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlResourceParser.START_TAG:
                            if (xmlResourceParser.getName().equals(FeaturesConfig.channel)) {

                                sIgnoreShowAppsList += xmlResourceParser.getAttributeValue(null, "packageName");
                                sIgnoreShowAppsList += "|";

                            }
                            break;
                        default:
                            break;
                    }
                    event = xmlResourceParser.next();
                }
                if (xmlResourceParser instanceof XmlResourceParser) {
                    ((XmlResourceParser) xmlResourceParser).close();
                }
            } catch (Exception e) {
                LogUtils.eTag("ignoreShowApp", e.getMessage());
            }

            //测试的版本，默认显示设置。方便调试
            if (FeaturesConfig.channel_fullname.contains("_test_")) {
                sIgnoreShowAppsList.replace("com.android.settings|", "");
            }

        }

//        LogUtils.dTag("ignoreShowApp", "ignore "+packageName);

//        isIgnoreAppByFast1(packageName);
//        isIgnoreAppByFast2(packageName);

        // 属于HotSeat的返回true
        if (HotSeatMgr.getInstance().isHotSeat(packageName)) {
            return true;
        }

        //去掉隐藏的也算
        Set<String> hideSet = SPUtils.getInstance().getStringSet("ddy_channel_icon");
        boolean ignore = (sIgnoreShowAppsList.indexOf(packageName + "|") != -1 || hideSet.contains(packageName));

        LogUtils.dTag("ignoreShowApp", "ignore " + packageName + " " + (ignore ? "true" : "false"));

        return ignore;
    }

    //需求：快速安装方案1》华为设备会预置N个应用。但默认不在桌面显示。所以，这里处理为第一次启动桌面时，这里判断到的应用，就默认当做是忽略显示的应用。
    private void isIgnoreAppByFast1(final String packageName) {
        //另：应用中心会以创建 应用快捷方式 的方式去创建桌面图标
        boolean firstStart = SPUtils.getInstance().getBoolean("first_start_launcher3_key", true);
        if (firstStart) {
            String firstStartValue = SPUtils.getInstance().getString("first_start_launcher3_value", "");
            firstStartValue += packageName;
            firstStartValue += "|";
            SPUtils.getInstance().put("first_start_launcher3_value", firstStartValue);

            sIgnoreShowAppsList += packageName;
            sIgnoreShowAppsList += "|";
        } else {
            String firstStartValue = SPUtils.getInstance().getString("first_start_launcher3_value", "");
            if (!sIgnoreShowAppsList.contains(firstStartValue))
                sIgnoreShowAppsList += firstStartValue;

            //第一次系统启动的应用，要出现图标的话，只有从应用市场或已上传应用中“打开”才会创建。这里不再显示了（仍当忽略）
//            //有出现系统重启后，所有图标都显示出来的问题（第二次启动做下过滤）
//            boolean bNewStart = SPUtils.getInstance().getBoolean("start_launcher3_key", true);
//            if(!bNewStart){
//                //这里当用户在之后去尝试安装系统预置应用时，因不会创建其桌面快捷方式，所以这里要做层过滤：即又得让它通过
//                //TODO:这里可能会跟快捷方式重复显示
//                if(firstStartValue.contains(packageName)){
//                    firstStartValue = firstStartValue.replace(packageName+"|", "");
//                    SPUtils.getInstance().put("first_start_launcher3_value", firstStartValue);
//
//                    sIgnoreShowAppsList = sIgnoreShowAppsList.replace(packageName+"|", "");
//                    LogUtils.dTag("ignoreShowApp", "firstStartValue pass "+packageName);
//                }
        }

    }

    //需求：快速安装方案2》会在/data/local/priv-app/**-1 下放对应游戏包，然后市场去安装时，创建软链接的方式至/data/app，然后再执行的pm scan-fast安装
    private static void isIgnoreAppByFast2(final String packageName) {
        if (sIgnoreShowAppsList.contains(packageName + "|"))
            return;

        String cmd = String.format("ls /data/app | grep %s", packageName + "-");
        ShellUtils2.CommandResult cmdResult = ShellUtils2.execCmd(cmd, true);

//        ToastUtils.showLong(cmd);
//        LogUtils.iTag("ignoreShowApp", "isIgnoreAppByFast2 a "+cmdResult.successMsg);
        if (cmdResult.successMsg.startsWith(packageName)) {
            String packageDirName = cmdResult.successMsg;

            //ls -l /data/app/com.game.sgz.huawei-1/base.apk
            cmd = String.format("ls -l /data/app/%s/base.apk", packageDirName);
            cmdResult = ShellUtils2.execCmd(cmd, true);

//            ToastUtils.showLong(cmd+"2222");
            Log.i("ignoreShowApp", "isIgnoreAppByFast2 " + packageDirName + " " + cmdResult.successMsg);
            //lrwxrwxrwx 1 root root 54 2018-11-12 15:53 /data/app/com.tencent.tmgp.sgame-1/base.apk -> /data/local/priv-app/com.tencent.tmgp.sgame-1/base.apk
            if (cmdResult.successMsg.startsWith("l")
                    && cmdResult.successMsg.contains(" /data/local/priv-app/")) {
                if (!sIgnoreShowAppsList.contains(packageName)) {
                    sIgnoreShowAppsList += packageName;
                    sIgnoreShowAppsList += "|";
                }
            }
        }
    }

    /**
     * 屏蔽图标生成
     *
     * @param packageName
     * @return
     */
    public static boolean isIgnoreShortcutApp(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        //在屏蔽列表的包名也屏蔽图标生成广播
        if (isIgnoreShowApp(packageName)) {
            return true;
        }
        if (sIgnoreShortcutAppsList == null) {
            sIgnoreShortcutAppsList = "";
            //解析xml配置
            try {
                XmlPullParser xmlResourceParser = SettingConfig.getInstance().getIgnoreShortcutApp();
                if (xmlResourceParser == null) {
                    xmlResourceParser = Utils.getApp().getResources().getXml(R.xml.ignore_shortcut_app);
                }
                int event = xmlResourceParser.getEventType();
                while (event != XmlResourceParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlResourceParser.START_TAG:
                            if (xmlResourceParser.getName().equals(FeaturesConfig.channel)) {

                                sIgnoreShortcutAppsList += xmlResourceParser.getAttributeValue(null, "packageName");
                                sIgnoreShortcutAppsList += "|";

                            }
                            break;
                        default:
                            break;
                    }
                    event = xmlResourceParser.next();
                }
                if (xmlResourceParser instanceof XmlResourceParser) {
                    ((XmlResourceParser) xmlResourceParser).close();
                }
            } catch (Exception e) {
                LogUtils.eTag("isIgnoreShortcutApp", e.getMessage());
            }
        }

        boolean ignore = sIgnoreShortcutAppsList.indexOf(packageName + "|") != -1;
        if (!ignore) {
            LogUtils.dTag("isIgnoreShortcutApp", "un ignore=" + packageName);
        }

        return ignore;
    }

    public void addAndBindAddedWorkspaceItems(final Context context,
                                              final ArrayList<? extends ItemInfo> workspaceApps) {

        addAndBindAddedWorkspaceItems(context, workspaceApps, false);
    }


    /**
     * Adds the provided items to the workspace.
     */
    public void addAndBindAddedWorkspaceItems(final Context context,
                                              final ArrayList<? extends ItemInfo> workspaceApps, final boolean isPresetAdd) {
        final Callbacks callbacks = getCallback();
        if (workspaceApps.isEmpty()) {
            return;
        }
        // Process the newly added applications and add them to the database first
        Runnable r = new Runnable() {
            public void run() {
                final ArrayList<ItemInfo> addedShortcutsFinal = new ArrayList<ItemInfo>();
                final ArrayList<Long> addedWorkspaceScreensFinal = new ArrayList<Long>();

                // Get the list of workspace screens.  We need to append to this list and
                // can not use sBgWorkspaceScreens because loadWorkspace() may not have been
                // called.
                ArrayList<Long> workspaceScreens = loadWorkspaceScreensDb(context);
                synchronized (sBgLock) {

                    for (ItemInfo item : workspaceApps) {

                        if (sIgnoreShowAppsList != null
                                && sIgnoreShowAppsList.contains(item.getIntent().getPackage() + "|")) {
                            Log.i("ignoreShowApp", item.getIntent().getPackage());
                            continue;
                        }

                        if (item instanceof ShortcutInfo) {
                            // Short-circuit this logic if the icon exists somewhere on the workspace
                            if (shortcutExists(context, item.getIntent(), item.user)) {
                                Log.i("shortcut", "shortcutExists " + item.title.toString());
                                continue;
                            }
                        } else {
//                            //TODO:这里变更为除非通过  应用市场或上传管理 打开 ，去创建快捷方式，否则不会有对应桌面图标。
//                            if(true){
//                                ToastUtils.showLong(R.string.launcher_cannot_add_appiconinfo);
//                                continue;
//                            }
                            String appPackage = item.getIntent().getComponent().getPackageName();
                            if (isIgnoreShowApp(appPackage)) {
                                handleHotSeat(item, appPackage);
                                continue;
                            }
                        }

                        // Find appropriate space for the item.
                        Pair<Long, int[]> coords = findSpaceForItem(context,
                                workspaceScreens, addedWorkspaceScreensFinal,
                                1, 1);
                        long screenId = coords.first;
                        int[] cordinates = coords.second;

                        ItemInfo itemInfo;
                        if (item instanceof ShortcutInfo || item instanceof FolderInfo) {
                            itemInfo = item;
                        } else if (item instanceof AppInfo) {
                            itemInfo = ((AppInfo) item).makeShortcut();
                        } else {
                            throw new RuntimeException("Unexpected info type");
                        }

//                             Add the shortcut to the db
                        addItemToDatabase(context, itemInfo,
                                LauncherSettings.Favorites.CONTAINER_DESKTOP,
                                screenId, cordinates[0], cordinates[1]);

                        // Save the ShortcutInfo for binding in the workspace
                        addedShortcutsFinal.add(itemInfo);
                    }

                    //目前所有图标扫描处理完毕
                    SPUtils.getInstance().put("first_start_launcher3_key", false);
                    SPUtils.getInstance().put("start_launcher3_key", false);
                }

                // Update the workspace screens
                updateWorkspaceScreenOrder(context, workspaceScreens);

                if (!addedShortcutsFinal.isEmpty()) {
                    runOnMainThread(new Runnable() {
                        public void run() {
                            Callbacks cb = getCallback();
                            if (callbacks == cb && cb != null) {
                                final ArrayList<ItemInfo> addAnimated = new ArrayList<ItemInfo>();
                                final ArrayList<ItemInfo> addNotAnimated = new ArrayList<ItemInfo>();
                                if (!addedShortcutsFinal.isEmpty()) {
                                    ItemInfo info = addedShortcutsFinal.get(addedShortcutsFinal.size() - 1);
                                    long lastScreenId = info.screenId;
                                    for (ItemInfo i : addedShortcutsFinal) {
                                        if (i.screenId == lastScreenId) {
                                            addAnimated.add(i);
                                        } else {
                                            addNotAnimated.add(i);
                                        }
                                    }
                                }
                                callbacks.bindAppsAdded(addedWorkspaceScreensFinal,
                                        addNotAnimated, addAnimated, null);
                            }
                        }
                    });
                }
            }

            /**
             * 支持hotseat应用动态安装显示
             * @param item
             * @param appPackage
             */
            private void handleHotSeat(ItemInfo item, String appPackage) {
                final ItemInfo localItemInfo = item;
                final String localPackage = appPackage;
                runOnMainThread(new Runnable() {
                    public void run() {
                        try {
                            if (HotSeatMgr.getInstance().isHotSeat(localPackage)) {
                                Launcher launcher = (Launcher) getCallback();
                                if (localItemInfo instanceof AppInfo) {
                                    HotSeatInfo info = HotSeatMgr.getInstance().getHotSeatInfo(localPackage);
                                    if (info != null) {
                                        localItemInfo.container = Long.parseLong(info.container);
                                        localItemInfo.screenId = Long.parseLong(info.screen);
                                        localItemInfo.cellX = Integer.parseInt(info.x);
                                        localItemInfo.cellY = Integer.parseInt(info.y);
                                        ShortcutInfo shortcutInfo = ((AppInfo) localItemInfo).makeShortcut();
                                        launcher.addHotSeat(localItemInfo, shortcutInfo);
                                        //  Add the shortcut to the db
                                        addItemToDatabase(context, shortcutInfo,
                                                LauncherSettings.Favorites.CONTAINER_HOTSEAT,
                                                localItemInfo.screenId, localItemInfo.cellX, localItemInfo.cellY);
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        runOnWorkerThread(r);
    }

    private void unbindItemInfosAndClearQueuedBindRunnables() {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            throw new RuntimeException("Expected unbindLauncherItemInfos() to be called from the " +
                    "main thread");
        }

        // Clear any deferred bind runnables
        synchronized (mDeferredBindRunnables) {
            mDeferredBindRunnables.clear();
        }

        // Remove any queued UI runnables
        mHandler.cancelAll();
        // Unbind all the workspace items
        unbindWorkspaceItemsOnMainThread();
    }

    /**
     * Unbinds all the sBgWorkspaceItems and sBgAppWidgets on the main thread
     */
    void unbindWorkspaceItemsOnMainThread() {
        // Ensure that we don't use the same workspace items data structure on the main thread
        // by making a copy of workspace items first.
        final ArrayList<ItemInfo> tmpItems = new ArrayList<ItemInfo>();
        synchronized (sBgLock) {
            tmpItems.addAll(sBgWorkspaceItems);
            tmpItems.addAll(sBgAppWidgets);
        }
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (ItemInfo item : tmpItems) {
                    item.unbind();
                }
            }
        };
        runOnMainThread(r);
    }

    /**
     * Adds an item to the DB if it was not created previously, or move it to a new
     * <container, screen, cellX, cellY>
     */
    static void addOrMoveItemInDatabase(Context context, ItemInfo item, long container,
                                        long screenId, int cellX, int cellY) {
        if (item.container == ItemInfo.NO_ID) {
            // From all apps
            addItemToDatabase(context, item, container, screenId, cellX, cellY);
        } else {
            // From somewhere else
            moveItemInDatabase(context, item, container, screenId, cellX, cellY);
        }
    }

    void addOrMoveItemInDatabaseById(Context context, ItemInfo item, long container,
                                     long screenId, int cellX, int cellY) {
        synchronized (sBgLock) {
            if (item.id == ItemInfo.NO_ID) {
                addItemToDatabaseDirect(context, item, container, screenId, cellX, cellY);
                putPackageMap((ShortcutInfo) item);
            } else {
                moveItemInDatabaseDirect(context, item, container, screenId, cellX, cellY);
                modifyPackageMap((ShortcutInfo) item);
            }
        }
    }

    static void checkItemInfoLocked(
            final long itemId, final ItemInfo item, StackTraceElement[] stackTrace) {
        /**
         * https://bugly.qq.com/v2/crash-reporting/crashes/f5917470cb/302?pid=1#
         * 不知道为什么抛出异常，直接注释
         * */
//        ItemInfo modelItem = sBgItemsIdMap.get(itemId);
//        if (modelItem != null && item != modelItem) {
//            // check all the data is consistent
//            if (modelItem instanceof ShortcutInfo && item instanceof ShortcutInfo) {
//                ShortcutInfo modelShortcut = (ShortcutInfo) modelItem;
//                ShortcutInfo shortcut = (ShortcutInfo) item;
//                if (modelShortcut.title.toString().equals(shortcut.title.toString()) &&
//                        modelShortcut.intent.filterEquals(shortcut.intent) &&
//                        modelShortcut.id == shortcut.id &&
//                        modelShortcut.itemType == shortcut.itemType &&
//                        modelShortcut.container == shortcut.container &&
//                        modelShortcut.screenId == shortcut.screenId &&
//                        modelShortcut.cellX == shortcut.cellX &&
//                        modelShortcut.cellY == shortcut.cellY &&
//                        modelShortcut.spanX == shortcut.spanX &&
//                        modelShortcut.spanY == shortcut.spanY &&
//                        ((modelShortcut.dropPos == null && shortcut.dropPos == null) ||
//                        (modelShortcut.dropPos != null &&
//                                shortcut.dropPos != null &&
//                                modelShortcut.dropPos[0] == shortcut.dropPos[0] &&
//                        modelShortcut.dropPos[1] == shortcut.dropPos[1]))) {
//                    // For all intents and purposes, this is the same object
//                    return;
//                }
//            }
//
//            // the modelItem needs to match up perfectly with item if our model is
//            // to be consistent with the database-- for now, just require
//            // modelItem == item or the equality check above
//            String msg = "item: " + ((item != null) ? item.toString() : "null") +
//                    "modelItem: " +
//                    ((modelItem != null) ? modelItem.toString() : "null") +
//                    "Error: ItemInfo passed to checkItemInfo doesn't match original";
//            RuntimeException e = new RuntimeException(msg);
//            if (stackTrace != null) {
//                e.setStackTrace(stackTrace);
//            }
//            throw e;
//        }
    }

    static void checkItemInfo(final ItemInfo item) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        final long itemId = item.id;
        Runnable r = new Runnable() {
            public void run() {
                synchronized (sBgLock) {
                    checkItemInfoLocked(itemId, item, stackTrace);
                }
            }
        };
        runOnWorkerThread(r);
    }

    static void updateItemInDatabaseHelper(Context context, final ContentValues values,
                                           final ItemInfo item, final String callingFunction) {
        final long itemId = item.id;
        final Uri uri = LauncherSettings.Favorites.getContentUri(itemId);
        final ContentResolver cr = context.getContentResolver();

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Runnable r = new Runnable() {
            public void run() {
                cr.update(uri, values, null, null);
                updateItemArrays(item, itemId, stackTrace);
            }
        };
        runOnWorkerThread(r);
    }

    static void updateItemsInDatabaseHelper(Context context, final ArrayList<ContentValues> valuesList,
                                            final ArrayList<ItemInfo> items, final String callingFunction) {
        final ContentResolver cr = context.getContentResolver();

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Runnable r = new Runnable() {
            public void run() {
                ArrayList<ContentProviderOperation> ops =
                        new ArrayList<ContentProviderOperation>();
                int count = items.size();
                for (int i = 0; i < count; i++) {
                    ItemInfo item = items.get(i);
                    final long itemId = item.id;
                    final Uri uri = LauncherSettings.Favorites.getContentUri(itemId);
                    ContentValues values = valuesList.get(i);

                    ops.add(ContentProviderOperation.newUpdate(uri).withValues(values).build());
                    updateItemArrays(item, itemId, stackTrace);
                }
                try {
                    cr.applyBatch(LauncherProvider.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        runOnWorkerThread(r);
    }

    static void updateItemArrays(ItemInfo item, long itemId, StackTraceElement[] stackTrace) {
        // Lock on mBgLock *after* the db operation
        synchronized (sBgLock) {
            checkItemInfoLocked(itemId, item, stackTrace);

            if (item.container != LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                    item.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                // Item is in a folder, make sure this folder exists
                if (!sBgFolders.containsKey(item.container)) {
                    // An items container is being set to a that of an item which is not in
                    // the list of Folders.
                    String msg = "item: " + item + " container being set to: " +
                            item.container + ", not in the list of folders";
                    Log.e(TAG, msg);
                }
            }

            // Items are added/removed from the corresponding FolderInfo elsewhere, such
            // as in Workspace.onDrop. Here, we just add/remove them from the list of items
            // that are on the desktop, as appropriate
            ItemInfo modelItem = sBgItemsIdMap.get(itemId);
            if (modelItem != null &&
                    (modelItem.container == LauncherSettings.Favorites.CONTAINER_DESKTOP ||
                            modelItem.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT)) {
                switch (modelItem.itemType) {
                    case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                    case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                    case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                        if (!sBgWorkspaceItems.contains(modelItem)) {
                            sBgWorkspaceItems.add(modelItem);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                sBgWorkspaceItems.remove(modelItem);
            }
        }
    }

    /**
     * Move an item in the DB to a new <container, screen, cellX, cellY>
     */
    public static void moveItemInDatabase(Context context, final ItemInfo item, final long container,
                                          final long screenId, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;

        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        if (context instanceof Launcher && screenId < 0 &&
                container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }

        final ContentValues values = new ContentValues();
        values.put(LauncherSettings.Favorites.CONTAINER, item.container);
        values.put(LauncherSettings.Favorites.CELLX, item.cellX);
        values.put(LauncherSettings.Favorites.CELLY, item.cellY);
        values.put(LauncherSettings.Favorites.RANK, item.rank);
        values.put(LauncherSettings.Favorites.SCREEN, item.screenId);

        updateItemInDatabaseHelper(context, values, item, "moveItemInDatabase");
    }

    public static void moveItemInDatabaseDirect(Context context, final ItemInfo item, final long container,
                                                final long screenId, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;

        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        if (context instanceof Launcher && screenId < 0 &&
                container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }

        final ContentValues values = new ContentValues();
        values.put(LauncherSettings.Favorites.TITLE, TextUtils.isEmpty(item.title) ? "" : item.title.toString());
        values.put(LauncherSettings.Favorites.CONTAINER, item.container);
        values.put(LauncherSettings.Favorites.CELLX, item.cellX);
        values.put(LauncherSettings.Favorites.CELLY, item.cellY);
        values.put(LauncherSettings.Favorites.RANK, item.rank);
        values.put(LauncherSettings.Favorites.SCREEN, item.screenId);
        if (item instanceof ShortcutInfo && item.getIntent() != null)
            values.put(LauncherSettings.Favorites.INTENT, item.getIntent().toUri(0));
        if (!TextUtils.isEmpty(item.apkObsUrl))
            values.put(LauncherSettings.Favorites.DOWNLOAD_URL, item.apkObsUrl);

        final long itemId = item.id;
        final Uri uri = LauncherSettings.Favorites.getContentUri(itemId);
        final ContentResolver cr = context.getContentResolver();

        cr.update(uri, values, null, null);
    }

    /**
     * Move items in the DB to a new <container, screen, cellX, cellY>. We assume that the
     * cellX, cellY have already been updated on the ItemInfos.
     */
    static void moveItemsInDatabase(Context context, final ArrayList<ItemInfo> items,
                                    final long container, final int screen) {

        ArrayList<ContentValues> contentValues = new ArrayList<ContentValues>();
        int count = items.size();

        for (int i = 0; i < count; i++) {
            ItemInfo item = items.get(i);
            item.container = container;

            // We store hotseat items in canonical form which is this orientation invariant position
            // in the hotseat
            if (context instanceof Launcher && screen < 0 &&
                    container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(item.cellX,
                        item.cellY);
            } else {
                item.screenId = screen;
            }

            final ContentValues values = new ContentValues();
            values.put(LauncherSettings.Favorites.CONTAINER, item.container);
            values.put(LauncherSettings.Favorites.CELLX, item.cellX);
            values.put(LauncherSettings.Favorites.CELLY, item.cellY);
            values.put(LauncherSettings.Favorites.RANK, item.rank);
            values.put(LauncherSettings.Favorites.SCREEN, item.screenId);

            contentValues.add(values);
        }
        updateItemsInDatabaseHelper(context, contentValues, items, "moveItemInDatabase");
    }

    /**
     * Move and/or resize item in the DB to a new <container, screen, cellX, cellY, spanX, spanY>
     */
    static void modifyItemInDatabase(Context context, final ItemInfo item, final long container,
                                     final long screenId, final int cellX, final int cellY, final int spanX, final int spanY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.spanX = spanX;
        item.spanY = spanY;

        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        if (context instanceof Launcher && screenId < 0 &&
                container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }

        final ContentValues values = new ContentValues();
        values.put(LauncherSettings.Favorites.CONTAINER, item.container);
        values.put(LauncherSettings.Favorites.CELLX, item.cellX);
        values.put(LauncherSettings.Favorites.CELLY, item.cellY);
        values.put(LauncherSettings.Favorites.RANK, item.rank);
        values.put(LauncherSettings.Favorites.SPANX, item.spanX);
        values.put(LauncherSettings.Favorites.SPANY, item.spanY);
        values.put(LauncherSettings.Favorites.SCREEN, item.screenId);

        updateItemInDatabaseHelper(context, values, item, "modifyItemInDatabase");
    }

    /**
     * modifyItemInDatabase()改的方法，增加item到sBgItemsIdMap的逻辑，保证findSpaceForItem时参数的正确
     */
    static void modifyItemInDatabaseAndAddWorkspace(Context context, final ItemInfo item, final long container,
                                                    final long screenId, final int cellX, final int cellY, final int spanX, final int spanY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.spanX = spanX;
        item.spanY = spanY;

        if (context instanceof Launcher && screenId < 0 &&
                container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }

        final ContentValues values = new ContentValues();
        values.put(LauncherSettings.Favorites.CONTAINER, item.container);
        values.put(LauncherSettings.Favorites.CELLX, item.cellX);
        values.put(LauncherSettings.Favorites.CELLY, item.cellY);
        values.put(LauncherSettings.Favorites.RANK, item.rank);
        values.put(LauncherSettings.Favorites.SPANX, item.spanX);
        values.put(LauncherSettings.Favorites.SPANY, item.spanY);
        values.put(LauncherSettings.Favorites.SCREEN, item.screenId);

        final long itemId = item.id;
        final Uri uri = LauncherSettings.Favorites.getContentUri(itemId);
        final ContentResolver cr = context.getContentResolver();
        cr.update(uri, values, null, null);

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        synchronized (sBgLock) {
            checkItemInfoLocked(item.id, item, stackTrace);
            sBgItemsIdMap.put(item.id, item);
        }
    }

    static void modifyItemInDatabase(Context context, final ItemInfo item, final long container,
                                     final long screenId, final int cellX, final int cellY, final int spanX, final int spanY, String contentDescription) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        item.spanX = spanX;
        item.spanY = spanY;

        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        if (context instanceof Launcher && screenId < 0 &&
                container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }

        final ContentValues values = new ContentValues();
        values.put(LauncherSettings.Favorites.CONTAINER, item.container);
        values.put(LauncherSettings.Favorites.CELLX, item.cellX);
        values.put(LauncherSettings.Favorites.CELLY, item.cellY);
        values.put(LauncherSettings.Favorites.RANK, item.rank);
        values.put(LauncherSettings.Favorites.SPANX, item.spanX);
        values.put(LauncherSettings.Favorites.SPANY, item.spanY);
        values.put(LauncherSettings.Favorites.SCREEN, item.screenId);
        values.put(LauncherSettings.Favorites.INTENT, contentDescription);

        updateItemInDatabaseHelper(context, values, item, "modifyItemInDatabase");
    }

    /**
     * Update an item to the database in a specified container.
     */
    public static void updateItemInDatabase(Context context, final ItemInfo item) {
        final ContentValues values = new ContentValues();
        item.onAddToDatabase(context, values);
        updateItemInDatabaseHelper(context, values, item, "updateItemInDatabase");
    }

    private void assertWorkspaceLoaded() {
        if (LauncherAppState.isDogfoodBuild()) {
            synchronized (mLock) {
                if (!mHasLoaderCompletedOnce ||
                        (mLoaderTask != null && mLoaderTask.mIsLoadingAndBindingWorkspace)) {
                    throw new RuntimeException("Trying to add shortcut while loader is running");
                }
            }
        }
    }

    /**
     * Returns true if the shortcuts already exists on the workspace. This must be called after
     * the workspace has been loaded. We identify a shortcut by its intent.
     */
    @Thunk
    boolean shortcutExists(Context context, Intent intent, UserHandleCompat user) {
        assertWorkspaceLoaded();
        final String intentWithPkg, intentWithoutPkg;
        if (intent.getComponent() != null) {
            // If component is not null, an intent with null package will produce
            // the same result and should also be a match.
            String packageName = intent.getComponent().getPackageName();
            if (intent.getPackage() != null) {
                intentWithPkg = intent.toUri(0);
                intentWithoutPkg = new Intent(intent).setPackage(null).toUri(0);
            } else {
                intentWithPkg = new Intent(intent).setPackage(packageName).toUri(0);
                intentWithoutPkg = intent.toUri(0);
            }
        } else {
            intentWithPkg = intent.toUri(0);
            intentWithoutPkg = intent.toUri(0);
        }

        synchronized (sBgLock) {
            for (ItemInfo item : sBgItemsIdMap) {
                if (item instanceof ShortcutInfo) {
                    ShortcutInfo info = (ShortcutInfo) item;
                    Intent targetIntent = info.promisedIntent == null
                            ? info.intent : info.promisedIntent;
                    if (targetIntent != null && info.user.equals(user)) {
                        String s = targetIntent.toUri(0);
                        if (intentWithPkg.equals(s) || intentWithoutPkg.equals(s)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Thunk
    int presetShortcutExists(Context context, Intent intent) {
        String addShortcutPackageName = null;
        String exisShortcutPackageName = null;

        if (intent.getComponent() != null) {
            addShortcutPackageName = intent.getComponent().getPackageName();
        }

        synchronized (sBgLock) {
            for (ItemInfo item : sBgItemsIdMap) {
                if (item instanceof ShortcutInfo) {
                    if (item.itemType != LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT)
                        continue;
                    ShortcutInfo info = (ShortcutInfo) item;
                    exisShortcutPackageName = info.getIntent().getPackage();
                    if (addShortcutPackageName != null
                            && exisShortcutPackageName != null
                            && addShortcutPackageName.equals(exisShortcutPackageName)) {
                        return sBgItemsIdMap.indexOfValue(item);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Find a folder in the db, creating the FolderInfo if necessary, and adding it to folderList.
     */
    FolderInfo getFolderById(Context context, LongArrayMap<FolderInfo> folderList, long id) {
        final ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI, null,
                "_id=? and (itemType=? or itemType=?)",
                new String[]{String.valueOf(id),
                        String.valueOf(LauncherSettings.Favorites.ITEM_TYPE_FOLDER)}, null);

        try {
            if (c.moveToFirst()) {
                final int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.ITEM_TYPE);
                final int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.TITLE);
                final int containerIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CONTAINER);
                final int screenIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
                final int cellXIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
                final int cellYIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
                final int optionsIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites.OPTIONS);

                FolderInfo folderInfo = null;
                switch (c.getInt(itemTypeIndex)) {
                    case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                        folderInfo = findOrMakeFolder(folderList, id);
                        break;
                }

                // Do not trim the folder label, as is was set by the user.
                folderInfo.title = c.getString(titleIndex);
                folderInfo.id = id;
                folderInfo.container = c.getInt(containerIndex);
                folderInfo.screenId = c.getInt(screenIndex);
                folderInfo.cellX = c.getInt(cellXIndex);
                folderInfo.cellY = c.getInt(cellYIndex);
                folderInfo.options = c.getInt(optionsIndex);

                return folderInfo;
            }
        } finally {
            c.close();
        }

        return null;
    }

    /**
     * Add an item to the database in a specified container. Sets the container, screen, cellX and
     * cellY fields of the item. Also assigns an ID to the item.
     */
    public static void addItemToDatabase(Context context, final ItemInfo item, final long container,
                                         final long screenId, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        if (context instanceof Launcher && screenId < 0 &&
                container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }

        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();
        item.onAddToDatabase(context, values);

        values.put(LauncherSettings.Favorites.TITLE, TextUtils.isEmpty(item.title) ? "" : item.title.toString());
        if (item instanceof ShortcutInfo && item.getIntent() != null)
            values.put(LauncherSettings.Favorites.INTENT, item.getIntent().toUri(0));

        item.id = LauncherAppState.getLauncherProvider().generateNewItemId();
        values.put(LauncherSettings.Favorites._ID, item.id);

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        Runnable r = new Runnable() {
            public void run() {
                cr.insert(LauncherSettings.Favorites.CONTENT_URI, values);

                // Lock on mBgLock *after* the db operation
                synchronized (sBgLock) {
                    checkItemInfoLocked(item.id, item, stackTrace);
                    sBgItemsIdMap.put(item.id, item);
                    switch (item.itemType) {
                        case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                            sBgFolders.put(item.id, (FolderInfo) item);
                            // Fall through
                        case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                        case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                        case LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT:
                            if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP ||
                                    item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                                sBgWorkspaceItems.add(item);
                            } else {
                                if (!sBgFolders.containsKey(item.container)) {
                                    // Adding an item to a folder that doesn't exist.
                                    String msg = "adding item: " + item + " to a folder that " +
                                            " doesn't exist";
                                    Log.e(TAG, msg);
                                }
                            }
                            break;
                        case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                            sBgAppWidgets.add((LauncherAppWidgetInfo) item);
                            break;
                    }
                }
            }
        };
        runOnWorkerThread(r);
    }

    public static void addItemToDatabaseDirect(Context context, final ItemInfo item, final long container,
                                               final long screenId, final int cellX, final int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        // We store hotseat items in canonical form which is this orientation invariant position
        // in the hotseat
        if (context instanceof Launcher && screenId < 0 &&
                container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            item.screenId = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        } else {
            item.screenId = screenId;
        }

        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();
        item.onAddToDatabase(context, values);

        if (item.id == ItemInfo.NO_ID) {
            item.id = LauncherAppState.getLauncherProvider().generateNewItemId();
        }
        values.put(LauncherSettings.Favorites._ID, item.id);

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        cr.insert(LauncherSettings.Favorites.CONTENT_URI, values);

        // Lock on mBgLock *after* the db operation
        synchronized (sBgLock) {
            checkItemInfoLocked(item.id, item, stackTrace);
            sBgItemsIdMap.put(item.id, item);
            switch (item.itemType) {
                case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                    sBgFolders.put(item.id, (FolderInfo) item);
                    // Fall through
                case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                case LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT:
                    if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP ||
                            item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                        sBgWorkspaceItems.add(item);
                    } else {
                        if (!sBgFolders.containsKey(item.container)) {
                            // Adding an item to a folder that doesn't exist.
                            String msg = "adding item: " + item.title + " to a folder that " +
                                    " doesn't exist";
                            Log.e(TAG, msg);
                        }
                    }
                    break;
                case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                    sBgAppWidgets.add((LauncherAppWidgetInfo) item);
                    break;
            }
        }
    }

    /**
     * Creates a new unique child id, for a given cell span across all layouts.
     */
    static int getCellLayoutChildId(
            long container, long screen, int localCellX, int localCellY, int spanX, int spanY) {
        return (((int) container & 0xFF) << 24)
                | ((int) screen & 0xFF) << 16 | (localCellX & 0xFF) << 8 | (localCellY & 0xFF);
    }

    private static ArrayList<ItemInfo> getItemsByPackageName(
            final String pn, final UserHandleCompat user) {
        ItemInfoFilter filter = new ItemInfoFilter() {
            @Override
            public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn) {
                return cn.getPackageName().equals(pn) && info.user.equals(user);
            }
        };
        return filterItemInfos(sBgItemsIdMap, filter);
    }

    /**
     * Removes all the items from the database corresponding to the specified package.
     */
    static void deletePackageFromDatabase(Context context, final String pn,
                                          final UserHandleCompat user) {
        deleteItemsFromDatabase(context, getItemsByPackageName(pn, user));
    }

    /**
     * Removes the specified item from the database
     *
     * @param context
     * @param item
     */
    public static void deleteItemFromDatabase(Context context, final ItemInfo item) {
        ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
        items.add(item);
        deleteItemsFromDatabase(context, items);
    }

    public static void deleteItemFromDatabaseDirect(Context context, final ItemInfo item) {
        final ContentResolver cr = context.getContentResolver();
        final Uri uri = LauncherSettings.Favorites.getContentUri(item.id);
        cr.delete(uri, null, null);

        // Lock on mBgLock *after* the db operation
        synchronized (sBgLock) {
            switch (item.itemType) {
                case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                    sFolderLocalMap.remove(item.title.toString());
                    sBgFolders.remove(item.id);
                    for (ItemInfo info : sBgItemsIdMap) {
                        if (info.container == item.id) {
                            // We are deleting a folder which still contains items that
                            // think they are contained by that folder.
                            String msg = "deleting a folder (" + item + ") which still " +
                                    "contains items (" + info + ")";
                            Log.e(TAG, msg);
                        }
                    }
                    sBgWorkspaceItems.remove(item);
                    break;
                case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                    sBgWorkspaceItems.remove(item);
                    break;
                case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                    sBgAppWidgets.remove((LauncherAppWidgetInfo) item);
                    break;
                case LauncherSettings.BaseLauncherColumns.ITEM_TYPE_PRESET_SHORTCUT:
                    sPresetItemsIdList.remove(item);
                    sBgWorkspaceItems.remove(item);
                    if (item.getIntent() != null && item.getIntent().getComponent() != null
                            && !TextUtils.isEmpty(item.getIntent().getComponent().getPackageName())) {
                        Log.e(TAG, "deleteItemFromDatabaseDirect package:" + item.getIntent().getComponent().getPackageName());
                        cr.delete(PresetFolderProvider.PresetFolder.getDeleteShortcutUri(
                                item.getIntent().getComponent().getPackageName()), null, null
                        );
                    }
                    break;
            }
            sBgItemsIdMap.remove(item.id);
        }
    }

    /**
     * Removes the specified items from the database
     *
     * @param context
     * @param items
     */
    static void deleteItemsFromDatabase(Context context, final ArrayList<? extends ItemInfo> items) {
        StringBuilder sb = new StringBuilder();
        for (ItemInfo itemInfo : items) {
            sb.append(itemInfo.id)
                    .append(",")
                    .append(itemInfo.title)
                    .append("\n");
        }
        if (items.isEmpty())
            return;
//        Log.i("shortcut", "deleteItemsFromDatabase items " + items.size());

        final ContentResolver cr = context.getContentResolver();
        Runnable r = new Runnable() {
            public void run() {
                Iterator<? extends ItemInfo> iterator = items.iterator();
                while (iterator.hasNext()) {
                    ItemInfo item = iterator.next();

                    final Uri uri = LauncherSettings.Favorites.getContentUri(item.id);
                    cr.delete(uri, null, null);

                    // Lock on mBgLock *after* the db operation
                    synchronized (sBgLock) {
                        switch (item.itemType) {
                            case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                                sFolderLocalMap.remove(item.title.toString());
                                sBgFolders.remove(item.id);
                                for (ItemInfo info : sBgItemsIdMap) {
                                    if (info.container == item.id) {
                                        // We are deleting a folder which still contains items that
                                        // think they are contained by that folder.
                                        String msg = "deleting a folder (" + item + ") which still " +
                                                "contains items (" + info + ")";
                                        Log.e(TAG, msg);
                                    }
                                }
                                sBgWorkspaceItems.remove(item);
                                break;
                            case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                            case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                                sBgWorkspaceItems.remove(item);
                                break;
                            case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                                sBgAppWidgets.remove((LauncherAppWidgetInfo) item);
                                break;
                            case LauncherSettings.BaseLauncherColumns.ITEM_TYPE_PRESET_SHORTCUT:
                                sPresetItemsIdList.remove(item);
                                sBgWorkspaceItems.remove(item);
                                if (item.getIntent() != null && item.getIntent().getComponent() != null
                                        && !TextUtils.isEmpty(item.getIntent().getComponent().getPackageName())) {
                                    Log.e(TAG, "deleteItemsFromDatabase package:" + item.getIntent().getComponent().getPackageName());
                                    cr.delete(PresetFolderProvider.PresetFolder.getDeleteShortcutUri(
                                            item.getIntent().getComponent().getPackageName()), null, null
                                    );
                                }
                                break;
                        }
                        sBgItemsIdMap.remove(item.id);
                    }
                }
            }
        };
        runOnWorkerThread(r);
    }

    /**
     * Update the order of the workspace screens in the database. The array list contains
     * a list of screen ids in the order that they should appear.
     */
    public void updateWorkspaceScreenOrder(Context context, final ArrayList<Long> screens) {
        final ArrayList<Long> screensCopy = new ArrayList<Long>(screens);
        final ContentResolver cr = context.getContentResolver();
        final Uri uri = LauncherSettings.WorkspaceScreens.CONTENT_URI;

        // Remove any negative screen ids -- these aren't persisted
        Iterator<Long> iter = screensCopy.iterator();
        while (iter.hasNext()) {
            long id = iter.next();
            if (id < 0) {
                iter.remove();
            }
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                // Clear the table
                ops.add(ContentProviderOperation.newDelete(uri).build());
                int count = screensCopy.size();
                for (int i = 0; i < count; i++) {
                    ContentValues v = new ContentValues();
                    long screenId = screensCopy.get(i);
                    v.put(LauncherSettings.WorkspaceScreens._ID, screenId);
                    v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, i);
                    ops.add(ContentProviderOperation.newInsert(uri).withValues(v).build());
                }

                try {
                    cr.applyBatch(LauncherProvider.AUTHORITY, ops);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                synchronized (sBgLock) {
                    sBgWorkspaceScreens.clear();
                    sBgWorkspaceScreens.addAll(screensCopy);
                }
            }
        };
        runOnWorkerThread(r);
    }

    public void updateWorkspaceScreenOrderDirect(Context context, final ArrayList<Long> screens) {
        final ArrayList<Long> screensCopy = new ArrayList<Long>(screens);
        final ContentResolver cr = context.getContentResolver();
        final Uri uri = LauncherSettings.WorkspaceScreens.CONTENT_URI;

        // Remove any negative screen ids -- these aren't persisted
        Iterator<Long> iter = screensCopy.iterator();
        while (iter.hasNext()) {
            long id = iter.next();
            if (id < 0) {
                iter.remove();
            }
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        // Clear the table
        ops.add(ContentProviderOperation.newDelete(uri).build());
        int count = screensCopy.size();
        for (int i = 0; i < count; i++) {
            ContentValues v = new ContentValues();
            long screenId = screensCopy.get(i);
            v.put(LauncherSettings.WorkspaceScreens._ID, screenId);
            v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, i);
            ops.add(ContentProviderOperation.newInsert(uri).withValues(v).build());
        }

        try {
            cr.applyBatch(LauncherProvider.AUTHORITY, ops);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        synchronized (sBgLock) {
            sBgWorkspaceScreens.clear();
            sBgWorkspaceScreens.addAll(screensCopy);
        }
    }

    /**
     * Remove the contents of the specified folder from the database
     */
    public static void deleteFolderContentsFromDatabase(Context context, final FolderInfo info) {
        final ContentResolver cr = context.getContentResolver();

        Runnable r = new Runnable() {
            public void run() {
                cr.delete(LauncherSettings.Favorites.getContentUri(info.id), null, null);
                // Lock on mBgLock *after* the db operation
                synchronized (sBgLock) {
                    sBgItemsIdMap.remove(info.id);
                    sBgFolders.remove(info.id);
                    sBgWorkspaceItems.remove(info);
                    sFolderLocalMap.remove(info.title.toString());
                }

                cr.delete(LauncherSettings.Favorites.CONTENT_URI,
                        LauncherSettings.Favorites.CONTAINER + "=" + info.id, null);
                // Lock on mBgLock *after* the db operation
                synchronized (sBgLock) {
                    for (ItemInfo childInfo : info.contents) {
                        sBgItemsIdMap.remove(childInfo.id);
                    }
                }
            }
        };
        runOnWorkerThread(r);
    }

    /**
     * Set this as the current Launcher activity object for the loader.
     */
    public void initialize(Callbacks callbacks) {
        synchronized (mLock) {
            // Disconnect any of the callbacks and drawables associated with ItemInfos on the
            // workspace to prevent leaking Launcher activities on orientation change.
            unbindItemInfosAndClearQueuedBindRunnables();
            mCallbacks = new WeakReference<Callbacks>(callbacks);
        }
    }

    @Override
    public void onPackageChanged(String packageName, UserHandleCompat user) {
        int op = PackageUpdatedTask.OP_UPDATE;
        enqueuePackageUpdated(new PackageUpdatedTask(op, new String[]{packageName},
                user));
    }

    @Override
    public void onPackageRemoved(String packageName, UserHandleCompat user) {
        int op = PackageUpdatedTask.OP_REMOVE;
        enqueuePackageUpdated(new PackageUpdatedTask(op, new String[]{packageName},
                user));
    }

    @Override
    public void onPackageAdded(String packageName, UserHandleCompat user) {
        int op = PackageUpdatedTask.OP_ADD;
        enqueuePackageUpdated(new PackageUpdatedTask(op, new String[]{packageName},
                user));

        Log.i("statistics", "onPackageAdded " + packageName);
        saveAppDataModel.statisticsAppInstall(packageName);
    }

    @Override
    public void onPackagesAvailable(String[] packageNames, UserHandleCompat user,
                                    boolean replacing) {
        if (!replacing) {
            enqueuePackageUpdated(new PackageUpdatedTask(PackageUpdatedTask.OP_ADD, packageNames,
                    user));
            if (mAppsCanBeOnRemoveableStorage) {
                // Only rebind if we support removable storage. It catches the
                // case where
                // apps on the external sd card need to be reloaded
                startLoaderFromBackground();
            }
        } else {
            // If we are replacing then just update the packages in the list
            enqueuePackageUpdated(new PackageUpdatedTask(PackageUpdatedTask.OP_UPDATE,
                    packageNames, user));
        }
    }

    @Override
    public void onPackagesUnavailable(String[] packageNames, UserHandleCompat user,
                                      boolean replacing) {
        if (!replacing) {
            enqueuePackageUpdated(new PackageUpdatedTask(
                    PackageUpdatedTask.OP_UNAVAILABLE, packageNames,
                    user));
        }
    }

    /**
     * Call from the handler for ACTION_PACKAGE_ADDED, ACTION_PACKAGE_REMOVED and
     * ACTION_PACKAGE_CHANGED.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG_RECEIVER) Log.d(TAG, "onReceive intent=" + intent);

        final String action = intent.getAction();
        if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
            // If we have changed locale we need to clear out the labels in all apps/workspace.
            forceReload();
        } else if (SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED.equals(action)) {
            Callbacks callbacks = getCallback();
            if (callbacks != null) {
                callbacks.bindSearchProviderChanged();
            }
        } else if (LauncherAppsCompat.ACTION_MANAGED_PROFILE_ADDED.equals(action)
                || LauncherAppsCompat.ACTION_MANAGED_PROFILE_REMOVED.equals(action)) {
            UserManagerCompat.getInstance(context).enableAndResetCache();
            forceReload();
        }
    }

    void forceReload() {
        resetLoadedState(true, true);

        // Do this here because if the launcher activity is running it will be restarted.
        // If it's not running startLoaderFromBackground will merely tell it that it needs
        // to reload.
        startLoaderFromBackground();
    }

    public void resetLoadedState(boolean resetAllAppsLoaded, boolean resetWorkspaceLoaded) {
        synchronized (mLock) {
            // Stop any existing loaders first, so they don't set mAllAppsLoaded or
            // mWorkspaceLoaded to true later
            stopLoaderLocked();
            if (resetAllAppsLoaded) mAllAppsLoaded = false;
            if (resetWorkspaceLoaded) mWorkspaceLoaded = false;
        }
    }

    /**
     * When the launcher is in the background, it's possible for it to miss paired
     * configuration changes.  So whenever we trigger the loader from the background
     * tell the launcher that it needs to re-run the loader when it comes back instead
     * of doing it now.
     */
    public void startLoaderFromBackground() {
        boolean runLoader = false;
        Callbacks callbacks = getCallback();
        if (callbacks != null) {
            // Only actually run the loader if they're not paused.
            if (!callbacks.setLoadOnResume()) {
                runLoader = true;
            }
        }
        if (runLoader) {
            startLoader(PagedView.INVALID_RESTORE_PAGE);
        }
    }

    /**
     * If there is already a loader task running, tell it to stop.
     */
    private void stopLoaderLocked() {
        LoaderTask oldTask = mLoaderTask;
        if (oldTask != null) {
            oldTask.stopLocked();
        }
    }

    public boolean isCurrentCallbacks(Callbacks callbacks) {
        return (mCallbacks != null && mCallbacks.get() == callbacks);
    }

    public void startLoader(int synchronousBindPage) {
        startLoader(synchronousBindPage, LOADER_FLAG_NONE);
    }

    public void startLoader(int synchronousBindPage, int loadFlags) {
        // Enable queue before starting loader. It will get disabled in Launcher#finishBindingItems
        InstallShortcutReceiver.enableInstallQueue();
        synchronized (mLock) {
            // Clear any deferred bind-runnables from the synchronized load process
            // We must do this before any loading/binding is scheduled below.
            synchronized (mDeferredBindRunnables) {
                mDeferredBindRunnables.clear();
            }

            // Don't bother to start the thread if we know it's not going to do anything
            if (mCallbacks != null && mCallbacks.get() != null) {
                // If there is already one running, tell it to stop.
                stopLoaderLocked();
                mLoaderTask = new LoaderTask(mApp.getContext(), loadFlags);
                if (synchronousBindPage != PagedView.INVALID_RESTORE_PAGE
                        && mAllAppsLoaded && mWorkspaceLoaded && !mIsLoaderTaskRunning) {
                    mLoaderTask.runBindSynchronousPage(synchronousBindPage);
                } else {
                    sWorkerThread.setPriority(Thread.NORM_PRIORITY);
                    sWorker.post(mLoaderTask);
                }
            }
        }
    }

    void bindRemainingSynchronousPages() {
        // Post the remaining side pages to be loaded
        if (!mDeferredBindRunnables.isEmpty()) {
            Runnable[] deferredBindRunnables = null;
            synchronized (mDeferredBindRunnables) {
                deferredBindRunnables = mDeferredBindRunnables.toArray(
                        new Runnable[mDeferredBindRunnables.size()]);
                mDeferredBindRunnables.clear();
            }
            for (final Runnable r : deferredBindRunnables) {
                mHandler.post(r);
            }
        }
    }

    public void stopLoader() {
        synchronized (mLock) {
            if (mLoaderTask != null) {
                mLoaderTask.stopLocked();
            }
        }
    }

    /**
     * Loads the workspace screen ids in an ordered list.
     */
    public static ArrayList<Long> loadWorkspaceScreensDb(Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri screensUri = LauncherSettings.WorkspaceScreens.CONTENT_URI;

        // Get screens ordered by rank.
        final Cursor sc = contentResolver.query(screensUri, null, null, null,
                LauncherSettings.WorkspaceScreens.SCREEN_RANK);
        ArrayList<Long> screenIds = new ArrayList<Long>();
        try {
            final int idIndex = sc.getColumnIndexOrThrow(LauncherSettings.WorkspaceScreens._ID);
            while (sc.moveToNext()) {
                try {
                    screenIds.add(sc.getLong(idIndex));
                } catch (Exception e) {
                    Launcher.addDumpLog(TAG, "Desktop items loading interrupted"
                            + " - invalid screens: " + e, true);
                }
            }
        } finally {
            sc.close();
        }
        return screenIds;
    }

    public boolean isAllAppsLoaded() {
        return mAllAppsLoaded;
    }

    /**
     * Runnable for the thread that loads the contents of the launcher:
     * - workspace icons
     * - widgets
     * - all apps icons
     */
    private class LoaderTask implements Runnable {
        private Context mContext;
        @Thunk
        boolean mIsLoadingAndBindingWorkspace;
        private boolean mStopped;
        @Thunk
        boolean mLoadAndBindStepFinished;
        private int mFlags;

        LoaderTask(Context context, int flags) {
            Log.i(TAG, "LoaderTask(Context context, int flags)");
            mContext = context;
            mFlags = flags;
        }

        private void loadAndBindWorkspace() {
            mIsLoadingAndBindingWorkspace = true;

            // Load the workspace
            if (DEBUG_LOADERS) {
                Log.d(TAG, "loadAndBindWorkspace mWorkspaceLoaded=" + mWorkspaceLoaded);
            }

            if (!mWorkspaceLoaded) {
                loadWorkspace();
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                    mWorkspaceLoaded = true;
                }
            }

            // Bind the workspace
            bindWorkspace(-1);
        }

        private void waitForIdle() {
            // Wait until the either we're stopped or the other threads are done.
            // This way we don't start loading all apps until the workspace has settled
            // down.
            synchronized (LoaderTask.this) {
                final long workspaceWaitTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

                mHandler.postIdle(new Runnable() {
                    public void run() {
                        synchronized (LoaderTask.this) {
                            mLoadAndBindStepFinished = true;
                            if (DEBUG_LOADERS) {
                                Log.d(TAG, "done with previous binding step");
                            }
                            LoaderTask.this.notify();
                        }
                    }
                });

                while (!mStopped && !mLoadAndBindStepFinished) {
                    try {
                        // Just in case mFlushingWorkerThread changes but we aren't woken up,
                        // wait no longer than 1sec at a time
                        this.wait(1000);
                    } catch (InterruptedException ex) {
                        // Ignore
                    }
                }
                if (DEBUG_LOADERS) {
                    Log.d(TAG, "waited "
                            + (SystemClock.uptimeMillis() - workspaceWaitTime)
                            + "ms for previous step to finish binding");
                }
            }
        }

        void runBindSynchronousPage(int synchronousBindPage) {
            if (synchronousBindPage == PagedView.INVALID_RESTORE_PAGE) {
                // Ensure that we have a valid page index to load synchronously
                throw new RuntimeException("Should not call runBindSynchronousPage() without " +
                        "valid page index");
            }
            if (!mAllAppsLoaded || !mWorkspaceLoaded) {
                // Ensure that we don't try and bind a specified page when the pages have not been
                // loaded already (we should load everything asynchronously in that case)
                throw new RuntimeException("Expecting AllApps and Workspace to be loaded");
            }
            synchronized (mLock) {
                if (mIsLoaderTaskRunning) {
                    // Ensure that we are never running the background loading at this point since
                    // we also touch the background collections
                    throw new RuntimeException("Error! Background loading is already running");
                }
            }

            // XXX: Throw an exception if we are already loading (since we touch the worker thread
            //      data structures, we can't allow any other thread to touch that data, but because
            //      this call is synchronous, we can get away with not locking).

            // The LauncherModel is static in the LauncherAppState and mHandler may have queued
            // operations from the previous activity.  We need to ensure that all queued operations
            // are executed before any synchronous binding work is done.
            mHandler.flush();

            // Divide the set of loaded items into those that we are binding synchronously, and
            // everything else that is to be bound normally (asynchronously).
            bindWorkspace(synchronousBindPage);
            // XXX: For now, continue posting the binding of AllApps as there are other issues that
            //      arise from that.
            onlyBindAllApps();
        }

        public void run() {
            synchronized (mLock) {
                if (mStopped) {
                    return;
                }
                mIsLoaderTaskRunning = true;
            }
            // Optimize for end-user experience: if the Launcher is up and // running with the
            // All Apps interface in the foreground, load All Apps first. Otherwise, load the
            // workspace first (default).
            keep_running:
            {
                if (DEBUG_LOADERS) Log.d(TAG, "step 1: loading workspace");
                loadAndBindWorkspace();

                if (mStopped) {
                    break keep_running;
                }

                waitForIdle();

                // second step
                if (DEBUG_LOADERS) Log.d(TAG, "step 2: loading all apps");
                loadAndBindAllApps();

                if (LauncherAppState.isDisableAllApps()) {
                    verifyApplications();
                }
            }

            // Clear out this reference, otherwise we end up holding it until all of the
            // callback runnables are done.
            mContext = null;

            synchronized (mLock) {
                // If we are still the last one to be scheduled, remove ourselves.
                if (mLoaderTask == this) {
                    mLoaderTask = null;
                }
                mIsLoaderTaskRunning = false;
                mHasLoaderCompletedOnce = true;
            }
        }

        private void verifyApplications() {
            final Context context = mApp.getContext();

            // Cross reference all the applications in our apps list with items in the workspace
            ArrayList<ItemInfo> tmpInfos;
            ArrayList<ItemInfo> added = new ArrayList<ItemInfo>();
            synchronized (sBgLock) {
                for (AppInfo app : mBgAllAppsList.data) {
                    if (isIgnoreShowApp(app.componentName.getPackageName())) {
                        continue;
                    }
                    tmpInfos = getItemInfoForComponentName(app.componentName, app.user);
                    if (tmpInfos.isEmpty()) {
                        // We are missing an application icon, so add this to the workspace
                        added.add(app);
                        // This is a rare event, so lets log it
                        Log.e(TAG, "Missing Application on load: " + app);
                    }
                }
            }
            if (!added.isEmpty()) {
                addAndBindAddedWorkspaceItems(context, added);//7.0 虽然去掉了去抽屉的代码，但留了这个方法给我们。
            }
        }

        public void stopLocked() {
            synchronized (LoaderTask.this) {
                mStopped = true;
                this.notify();
            }
        }

        /**
         * Gets the callbacks object.  If we've been stopped, or if the launcher object
         * has somehow been garbage collected, return null instead.  Pass in the Callbacks
         * object that was around when the deferred message was scheduled, and if there's
         * a new Callbacks object around then also return null.  This will save us from
         * calling onto it with data that will be ignored.
         */
        Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
            synchronized (mLock) {
                if (mStopped) {
                    return null;
                }

                if (mCallbacks == null) {
                    return null;
                }

                final Callbacks callbacks = mCallbacks.get();
                if (callbacks != oldCallbacks) {
                    return null;
                }
                if (callbacks == null) {
                    Log.w(TAG, "no mCallbacks");
                    return null;
                }

                return callbacks;
            }
        }

        private void loadWorkspace() {
            final long t = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

            final Context context = mContext;
            final ContentResolver contentResolver = context.getContentResolver();
            final PackageManager manager = context.getPackageManager();
            final boolean isSafeMode = manager.isSafeMode();
            final LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(context);
            final boolean isSdCardReady = context.registerReceiver(null,
                    new IntentFilter(StartupReceiver.SYSTEM_READY)) != null;

            LauncherAppState app = LauncherAppState.getInstance();
            InvariantDeviceProfile profile = app.getInvariantDeviceProfile();
            int countX = profile.numColumns;
            int countY = profile.numRows;

            if (MigrateFromRestoreTask.ENABLED && MigrateFromRestoreTask.shouldRunTask(mContext)) {
                long migrationStartTime = System.currentTimeMillis();
                Log.v(TAG, "Starting workspace migration after restore");
                try {
                    MigrateFromRestoreTask task = new MigrateFromRestoreTask(mContext);
                    // Clear the flags before starting the task, so that we do not run the task
                    // again, in case there was an uncaught error.
                    MigrateFromRestoreTask.clearFlags(mContext);
                    task.execute();
                } catch (Exception e) {
                    Log.e(TAG, "Error during grid migration", e);

                    // Clear workspace.
                    mFlags = mFlags | LOADER_FLAG_CLEAR_WORKSPACE;
                }
                Log.v(TAG, "Workspace migration completed in "
                        + (System.currentTimeMillis() - migrationStartTime));
            }

            if ((mFlags & LOADER_FLAG_CLEAR_WORKSPACE) != 0) {
                Launcher.addDumpLog(TAG, "loadWorkspace: resetting launcher database", true);
                LauncherAppState.getLauncherProvider().deleteDatabase();
            }

            Log.i(TAG, "mFlags:" + mFlags + ",(mFlags & LOADER_FLAG_MIGRATE_SHORTCUTS):" + (mFlags & LOADER_FLAG_MIGRATE_SHORTCUTS));
            if ((mFlags & LOADER_FLAG_MIGRATE_SHORTCUTS) != 0) {
                // append the user's Launcher2 shortcuts
                Launcher.addDumpLog(TAG, "loadWorkspace: migrating from launcher2", true);
                LauncherAppState.getLauncherProvider().migrateLauncher2Shortcuts();
            } else {
                // Make sure the default workspace is loaded
                Launcher.addDumpLog(TAG, "loadWorkspace: loading default favorites", true);
                LauncherAppState.getLauncherProvider().loadDefaultFavoritesIfNecessary();
            }

            synchronized (sBgLock) {
                clearSBgDataStructures();
                final HashMap<String, Integer> installingPkgs = PackageInstallerCompat
                        .getInstance(mContext).updateAndGetActiveSessionCache();
                sBgWorkspaceScreens.addAll(loadWorkspaceScreensDb(mContext));

                final ArrayList<Long> itemsToRemove = new ArrayList<Long>();
                final ArrayList<Long> restoredRows = new ArrayList<Long>();
                final Uri contentUri = LauncherSettings.Favorites.CONTENT_URI;
                if (DEBUG_LOADERS) Log.d(TAG, "loading model from " + contentUri);

                //从数据库查询图标数据
                final Cursor c = contentResolver.query(contentUri, null, null, null, null);

                // +1 for the hotseat (it can be larger than the workspace)
                // Load workspace in reverse order to ensure that latest items are loaded first (and
                // before any earlier duplicates)
                final LongArrayMap<ItemInfo[][]> occupied = new LongArrayMap<>();

                try {
                    final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
                    final int intentIndex = c.getColumnIndexOrThrow
                            (LauncherSettings.Favorites.INTENT);
                    final int titleIndex = c.getColumnIndexOrThrow
                            (LauncherSettings.Favorites.TITLE);
                    final int containerIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.CONTAINER);
                    final int itemTypeIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.ITEM_TYPE);
                    final int appWidgetIdIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.APPWIDGET_ID);
                    final int appWidgetProviderIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.APPWIDGET_PROVIDER);
                    final int screenIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.SCREEN);
                    final int cellXIndex = c.getColumnIndexOrThrow
                            (LauncherSettings.Favorites.CELLX);
                    final int cellYIndex = c.getColumnIndexOrThrow
                            (LauncherSettings.Favorites.CELLY);
                    final int spanXIndex = c.getColumnIndexOrThrow
                            (LauncherSettings.Favorites.SPANX);
                    final int spanYIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.SPANY);
                    final int rankIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.RANK);
                    final int restoredIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.RESTORED);
                    final int profileIdIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.PROFILE_ID);
                    final int optionsIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.OPTIONS);
                    final int downloadUrlIndex = c.getColumnIndexOrThrow(
                            LauncherSettings.Favorites.DOWNLOAD_URL);
                    final CursorIconInfo cursorIconInfo = new CursorIconInfo(c);

                    final LongSparseArray<UserHandleCompat> allUsers = new LongSparseArray<>();
                    for (UserHandleCompat user : mUserManager.getUserProfiles()) {
                        allUsers.put(mUserManager.getSerialNumberForUser(user), user);
                    }

                    ShortcutInfo info;
                    String intentDescription;
                    LauncherAppWidgetInfo appWidgetInfo;
                    int container;
                    long id;
                    long serialNumber;
                    Intent intent;
                    UserHandleCompat user;

                    while (!mStopped && c.moveToNext()) {
                        try {
                            int itemType = c.getInt(itemTypeIndex);
                            boolean restored = 0 != c.getInt(restoredIndex);
                            boolean allowMissingTarget = false;
                            container = c.getInt(containerIndex);

                            switch (itemType) {
                                case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                                case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                                case LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT:
                                    id = c.getLong(idIndex);
                                    intentDescription = c.getString(intentIndex);
                                    serialNumber = c.getInt(profileIdIndex);
                                    user = allUsers.get(serialNumber);
                                    int promiseType = c.getInt(restoredIndex);
                                    int disabledState = 0;
                                    boolean itemReplaced = false;
                                    if (user == null) {
                                        // User has been deleted remove the item.
                                        itemsToRemove.add(id);
                                        continue;
                                    }

                                    //验证数据是否有效，无效的直接跳过
                                    try {
                                        //将数据库的intent描述转换为intent
                                        intent = Intent.parseUri(intentDescription, 0);
                                        //获取组件
                                        ComponentName cn = intent.getComponent();
                                        //不对预置图标进行有效性验证
                                        if (cn != null && cn.getPackageName() != null && itemType != LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                                            boolean validPkg = launcherApps.isPackageEnabledForProfile(
                                                    cn.getPackageName(), user);
                                            boolean validComponent = validPkg &&
                                                    launcherApps.isActivityEnabledForProfile(cn, user);

                                            if (validComponent) {
                                                if (restored) {
                                                    // no special handling necessary for this item
                                                    restoredRows.add(id);
                                                    restored = false;
                                                }
                                            } else if (validPkg) {
                                                intent = null;
                                                if ((promiseType & ShortcutInfo.FLAG_AUTOINTALL_ICON) != 0) {
                                                    // We allow auto install apps to have their intent
                                                    // updated after an install.
                                                    intent = manager.getLaunchIntentForPackage(
                                                            cn.getPackageName());
                                                    if (intent != null) {
                                                        ContentValues values = new ContentValues();
                                                        values.put(LauncherSettings.Favorites.INTENT,
                                                                intent.toUri(0));
                                                        updateItem(id, values);
                                                    }
                                                }

                                                if (intent == null) {
                                                    // The app is installed but the component is no
                                                    // longer available.
                                                    Launcher.addDumpLog(TAG,
                                                            "Invalid component removed: " + cn, true);
                                                    itemsToRemove.add(id);
                                                    continue;
                                                } else {
                                                    // no special handling necessary for this item
                                                    restoredRows.add(id);
                                                    restored = false;
                                                }
                                            } else if (restored) {
                                                // Package is not yet available but might be
                                                // installed later.
                                                Launcher.addDumpLog(TAG,
                                                        "package not yet restored: " + cn, true);

                                                if ((promiseType & ShortcutInfo.FLAG_RESTORE_STARTED) != 0) {
                                                    // Restore has started once.
                                                } else if (installingPkgs.containsKey(cn.getPackageName())) {
                                                    // App restore has started. Update the flag
                                                    promiseType |= ShortcutInfo.FLAG_RESTORE_STARTED;
                                                    ContentValues values = new ContentValues();
                                                    values.put(LauncherSettings.Favorites.RESTORED,
                                                            promiseType);
                                                    updateItem(id, values);
                                                } else if ((promiseType & ShortcutInfo.FLAG_RESTORED_APP_TYPE) != 0) {
                                                    // This is a common app. Try to replace this.
                                                    int appType = CommonAppTypeParser.decodeItemTypeFromFlag(promiseType);
                                                    CommonAppTypeParser parser = new CommonAppTypeParser(id, appType, context);
                                                    if (parser.findDefaultApp()) {
                                                        // Default app found. Replace it.
                                                        intent = parser.parsedIntent;
                                                        cn = intent.getComponent();
                                                        ContentValues values = parser.parsedValues;
                                                        values.put(LauncherSettings.Favorites.RESTORED, 0);
                                                        updateItem(id, values);
                                                        restored = false;
                                                        itemReplaced = true;

                                                    } else if (REMOVE_UNRESTORED_ICONS) {
                                                        Launcher.addDumpLog(TAG,
                                                                "Unrestored package removed: " + cn, true);
                                                        itemsToRemove.add(id);
                                                        continue;
                                                    }
                                                } else if (REMOVE_UNRESTORED_ICONS) {
                                                    Launcher.addDumpLog(TAG,
                                                            "Unrestored package removed: " + cn, true);
                                                    itemsToRemove.add(id);
                                                    continue;
                                                }
                                            } else if (launcherApps.isAppEnabled(
                                                    manager, cn.getPackageName(),
                                                    PackageManager.GET_UNINSTALLED_PACKAGES)) {
                                                // Package is present but not available.
                                                allowMissingTarget = true;
                                                disabledState = ShortcutInfo.FLAG_DISABLED_NOT_AVAILABLE;
                                            } else if (!isSdCardReady) {
                                                // SdCard is not ready yet. Package might get available,
                                                // once it is ready.
                                                Launcher.addDumpLog(TAG, "Invalid package: " + cn
                                                        + " (check again later)", true);
                                                HashSet<String> pkgs = sPendingPackages.get(user);
                                                if (pkgs == null) {
                                                    pkgs = new HashSet<String>();
                                                    sPendingPackages.put(user, pkgs);
                                                }
                                                pkgs.add(cn.getPackageName());
                                                allowMissingTarget = true;
                                                // Add the icon on the workspace anyway.

                                            } else {
                                                // Do not wait for external media load anymore.
                                                // Log the invalid package, and remove it
                                                Launcher.addDumpLog(TAG,
                                                        "Invalid package removed: " + cn, true);
                                                itemsToRemove.add(id);
                                                continue;
                                            }
                                        } else if (cn == null) {
                                            // For shortcuts with no component, keep them as they are
                                            restoredRows.add(id);
                                            restored = false;
                                        }
                                    } catch (URISyntaxException e) {
                                        Launcher.addDumpLog(TAG,
                                                "Invalid uri: " + intentDescription, true);
                                        itemsToRemove.add(id);
                                        continue;
                                    }

                                    //大于0说明该图标位于某个文件夹内
                                    boolean useLowResIcon = container >= 0 &&
                                            c.getInt(rankIndex) >= FolderIcon.NUM_ITEMS_IN_PREVIEW;

                                    if (itemReplaced) {
                                        if (user.equals(UserHandleCompat.myUserHandle())) {
                                            info = getAppShortcutInfo(manager, intent, user, context, null,
                                                    cursorIconInfo.iconIndex, titleIndex,
                                                    false, useLowResIcon);
                                        } else {
                                            // Don't replace items for other profiles.
                                            itemsToRemove.add(id);
                                            continue;
                                        }
                                    } else if (restored) {
                                        if (user.equals(UserHandleCompat.myUserHandle())) {
                                            Launcher.addDumpLog(TAG,
                                                    "constructing info for partially restored package",
                                                    true);
                                            info = getRestoredItemInfo(c, titleIndex, intent,
                                                    promiseType, itemType, cursorIconInfo, context);
                                            intent = getRestoredItemIntent(c, context, intent);
                                        } else {
                                            // Don't restore items for other profiles.
                                            itemsToRemove.add(id);
                                            continue;
                                        }
                                    } else if (itemType ==
                                            LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
                                        info = getAppShortcutInfo(manager, intent, user, context, c,
                                                cursorIconInfo.iconIndex, titleIndex,
                                                allowMissingTarget, useLowResIcon);
                                    } else if (itemType ==
                                            LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                                        info = getPresetShortcutInfo(c, context, titleIndex, cursorIconInfo);

                                        // App shortcuts that used to be automatically added to Launcher
                                        // didn't always have the correct intent flags set, so do that
                                        // here
                                        if (intent.getAction() != null &&
                                                intent.getCategories() != null &&
                                                intent.getAction().equals(Intent.ACTION_MAIN) &&
                                                intent.getCategories().contains(Intent.CATEGORY_LAUNCHER)) {
                                            intent.addFlags(
                                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                        }
                                    } else {
                                        info = getShortcutInfo(c, context, titleIndex, cursorIconInfo);

                                        // App shortcuts that used to be automatically added to Launcher
                                        // didn't always have the correct intent flags set, so do that
                                        // here
                                        if (intent.getAction() != null &&
                                                intent.getCategories() != null &&
                                                intent.getAction().equals(Intent.ACTION_MAIN) &&
                                                intent.getCategories().contains(Intent.CATEGORY_LAUNCHER)) {
                                            intent.addFlags(
                                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                                            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                        }
                                    }

                                    if (info != null) {
                                        info.id = id;
                                        info.intent = intent;
                                        info.container = container;
                                        info.screenId = c.getInt(screenIndex);
                                        info.cellX = c.getInt(cellXIndex);
                                        info.cellY = c.getInt(cellYIndex);
                                        info.rank = c.getInt(rankIndex);
                                        info.spanX = 1;
                                        info.spanY = 1;
                                        info.apkObsUrl = c.getString(downloadUrlIndex);
                                        info.intent.putExtra(ItemInfo.EXTRA_PROFILE, serialNumber);
                                        if (info.promisedIntent != null) {
                                            info.promisedIntent.putExtra(ItemInfo.EXTRA_PROFILE, serialNumber);
                                        }
                                        info.isDisabled = disabledState;
                                        if (isSafeMode && !Utilities.isSystemApp(context, intent)) {
                                            info.isDisabled |= ShortcutInfo.FLAG_DISABLED_SAFEMODE;
                                        }

                                        // check & update map of what's occupied
                                        if (!checkItemPlacement(occupied, info, sBgWorkspaceScreens)) {
                                            itemsToRemove.add(id);
                                            break;
                                        }

                                        if (restored) {
                                            ComponentName cn = info.getTargetComponent();
                                            if (cn != null) {
                                                Integer progress = installingPkgs.get(cn.getPackageName());
                                                if (progress != null) {
                                                    info.setInstallProgress(progress);
                                                } else {
                                                    info.status &= ~ShortcutInfo.FLAG_INSTALL_SESSION_ACTIVE;
                                                }
                                            }
                                        }

                                        if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                                            sPresetItemsIdList.add(info);
                                        }

                                        switch (container) {
                                            case LauncherSettings.Favorites.CONTAINER_DESKTOP:
                                            case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
                                                sBgWorkspaceItems.add(info);
                                                break;
                                            default:
                                                // Item is in a user folder
                                                FolderInfo folderInfo =
                                                        findOrMakeFolder(sBgFolders, container);
                                                folderInfo.add(info);
                                                break;
                                        }
                                        sBgItemsIdMap.put(info.id, info);

                                    } else {
                                        throw new RuntimeException("Unexpected null ShortcutInfo");
                                    }
                                    break;

                                case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                                    id = c.getLong(idIndex);
                                    FolderInfo folderInfo = findOrMakeFolder(sBgFolders, id);

                                    // Do not trim the folder label, as is was set by the user.
                                    folderInfo.title = c.getString(titleIndex);
                                    folderInfo.id = id;
                                    folderInfo.container = container;
                                    folderInfo.screenId = c.getInt(screenIndex);
                                    folderInfo.cellX = c.getInt(cellXIndex);
                                    folderInfo.cellY = c.getInt(cellYIndex);
                                    folderInfo.spanX = 1;
                                    folderInfo.spanY = 1;
                                    folderInfo.options = c.getInt(optionsIndex);

                                    // check & update map of what's occupied
                                    if (!checkItemPlacement(occupied, folderInfo, sBgWorkspaceScreens)) {
                                        itemsToRemove.add(id);
                                        break;
                                    }

                                    switch (container) {
                                        case LauncherSettings.Favorites.CONTAINER_DESKTOP:
                                        case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
                                            sBgWorkspaceItems.add(folderInfo);
                                            break;
                                    }

                                    if (restored) {
                                        // no special handling required for restored folders
                                        restoredRows.add(id);
                                    }

                                    sBgItemsIdMap.put(folderInfo.id, folderInfo);
                                    sBgFolders.put(folderInfo.id, folderInfo);
                                    break;

                                case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                                case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_APPWIDGET:
                                    // Read all Launcher-specific widget details
                                    boolean customWidget = itemType ==
                                            LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_APPWIDGET;

                                    int appWidgetId = c.getInt(appWidgetIdIndex);
                                    serialNumber = c.getLong(profileIdIndex);
                                    String savedProvider = c.getString(appWidgetProviderIndex);
                                    id = c.getLong(idIndex);
                                    user = allUsers.get(serialNumber);
                                    if (user == null) {
                                        itemsToRemove.add(id);
                                        continue;
                                    }

                                    final ComponentName component =
                                            ComponentName.unflattenFromString(savedProvider);

                                    final int restoreStatus = c.getInt(restoredIndex);
                                    final boolean isIdValid = (restoreStatus &
                                            LauncherAppWidgetInfo.FLAG_ID_NOT_VALID) == 0;
                                    final boolean wasProviderReady = (restoreStatus &
                                            LauncherAppWidgetInfo.FLAG_PROVIDER_NOT_READY) == 0;

                                    final LauncherAppWidgetProviderInfo provider =
                                            LauncherModel.getProviderInfo(context,
                                                    ComponentName.unflattenFromString(savedProvider),
                                                    user);

                                    final boolean isProviderReady = isValidProvider(provider);
                                    if (!isSafeMode && !customWidget &&
                                            wasProviderReady && !isProviderReady) {
                                        String log = "Deleting widget that isn't installed anymore: "
                                                + "id=" + id + " appWidgetId=" + appWidgetId;

                                        Log.e(TAG, log);
                                        Launcher.addDumpLog(TAG, log, false);
                                        itemsToRemove.add(id);
                                    } else {
                                        if (isProviderReady) {
                                            appWidgetInfo = new LauncherAppWidgetInfo(appWidgetId,
                                                    provider.provider);

                                            // The provider is available. So the widget is either
                                            // available or not available. We do not need to track
                                            // any future restore updates.
                                            int status = restoreStatus &
                                                    ~LauncherAppWidgetInfo.FLAG_RESTORE_STARTED;
                                            if (!wasProviderReady) {
                                                // If provider was not previously ready, update the
                                                // status and UI flag.

                                                // Id would be valid only if the widget restore broadcast was received.
                                                if (isIdValid) {
                                                    status = LauncherAppWidgetInfo.FLAG_UI_NOT_READY;
                                                } else {
                                                    status &= ~LauncherAppWidgetInfo
                                                            .FLAG_PROVIDER_NOT_READY;
                                                }
                                            }
                                            appWidgetInfo.restoreStatus = status;
                                        } else {
                                            Log.v(TAG, "Widget restore pending id=" + id
                                                    + " appWidgetId=" + appWidgetId
                                                    + " status =" + restoreStatus);
                                            appWidgetInfo = new LauncherAppWidgetInfo(appWidgetId,
                                                    component);
                                            appWidgetInfo.restoreStatus = restoreStatus;
                                            Integer installProgress = installingPkgs.get(component.getPackageName());

                                            if ((restoreStatus & LauncherAppWidgetInfo.FLAG_RESTORE_STARTED) != 0) {
                                                // Restore has started once.
                                            } else if (installProgress != null) {
                                                // App restore has started. Update the flag
                                                appWidgetInfo.restoreStatus |=
                                                        LauncherAppWidgetInfo.FLAG_RESTORE_STARTED;
                                            } else if (REMOVE_UNRESTORED_ICONS && !isSafeMode) {
                                                Launcher.addDumpLog(TAG,
                                                        "Unrestored widget removed: " + component, true);
                                                itemsToRemove.add(id);
                                                continue;
                                            }

                                            appWidgetInfo.installProgress =
                                                    installProgress == null ? 0 : installProgress;
                                        }

                                        appWidgetInfo.id = id;
                                        appWidgetInfo.screenId = c.getInt(screenIndex);
                                        appWidgetInfo.cellX = c.getInt(cellXIndex);
                                        appWidgetInfo.cellY = c.getInt(cellYIndex);
                                        appWidgetInfo.spanX = c.getInt(spanXIndex);
                                        appWidgetInfo.spanY = c.getInt(spanYIndex);
                                        appWidgetInfo.user = user;

                                        if (container != LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                                                container != LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                                            Log.e(TAG, "Widget found where container != " +
                                                    "CONTAINER_DESKTOP nor CONTAINER_HOTSEAT - ignoring!");
                                            itemsToRemove.add(id);
                                            continue;
                                        }

                                        appWidgetInfo.container = container;
                                        // check & update map of what's occupied
                                        if (!checkItemPlacement(occupied, appWidgetInfo, sBgWorkspaceScreens)) {
                                            itemsToRemove.add(id);
                                            break;
                                        }

                                        if (!customWidget) {
                                            String providerName =
                                                    appWidgetInfo.providerName.flattenToString();
                                            if (!providerName.equals(savedProvider) ||
                                                    (appWidgetInfo.restoreStatus != restoreStatus)) {
                                                ContentValues values = new ContentValues();
                                                values.put(
                                                        LauncherSettings.Favorites.APPWIDGET_PROVIDER,
                                                        providerName);
                                                values.put(LauncherSettings.Favorites.RESTORED,
                                                        appWidgetInfo.restoreStatus);
                                                updateItem(id, values);
                                            }
                                        }
                                        sBgItemsIdMap.put(appWidgetInfo.id, appWidgetInfo);
                                        sBgAppWidgets.add(appWidgetInfo);
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            Launcher.addDumpLog(TAG, "Desktop items loading interrupted", e, true);
                        }
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }

                // Break early if we've stopped loading
                if (mStopped) {
                    clearSBgDataStructures();
                    return;
                }

                if (itemsToRemove.size() > 0) {
                    // Remove dead items
                    contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI,
                            Utilities.createDbSelectionQuery(
                                    LauncherSettings.Favorites._ID, itemsToRemove), null);
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "Removed = " + Utilities.createDbSelectionQuery(
                                LauncherSettings.Favorites._ID, itemsToRemove));
                    }

                    // Remove any empty folder
                    for (long folderId : LauncherAppState.getLauncherProvider()
                            .deleteEmptyFolders()) {
                        sBgWorkspaceItems.remove(sBgFolders.get(folderId));
                        sBgFolders.remove(folderId);
                        sBgItemsIdMap.remove(folderId);
                    }
                }

                // Sort all the folder items and make sure the first 3 items are high resolution.
                for (FolderInfo folder : sBgFolders) {
                    Collections.sort(folder.contents, Folder.ITEM_POS_COMPARATOR);
                    int pos = 0;
                    for (ShortcutInfo info : folder.contents) {
                        if (info.usingLowResIcon) {
                            info.updateIcon(mIconCache, false);
                        }
                        pos++;
                        if (pos >= FolderIcon.NUM_ITEMS_IN_PREVIEW) {
                            break;
                        }
                    }
                }

                if (restoredRows.size() > 0) {
                    // Update restored items that no longer require special handling
                    ContentValues values = new ContentValues();
                    values.put(LauncherSettings.Favorites.RESTORED, 0);
                    contentResolver.update(LauncherSettings.Favorites.CONTENT_URI, values,
                            Utilities.createDbSelectionQuery(
                                    LauncherSettings.Favorites._ID, restoredRows), null);
                }

                if (!isSdCardReady && !sPendingPackages.isEmpty()) {
                    context.registerReceiver(new AppsAvailabilityCheck(),
                            new IntentFilter(StartupReceiver.SYSTEM_READY),
                            null, sWorker);
                }

                // Remove any empty screens
                ArrayList<Long> unusedScreens = new ArrayList<Long>(sBgWorkspaceScreens);
                for (ItemInfo item : sBgItemsIdMap) {
                    long screenId = item.screenId;
                    if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                            unusedScreens.contains(screenId)) {
                        unusedScreens.remove(screenId);
                    }
                }

                for (ItemInfo item : sPresetItemsIdList) {
                    long screenId = item.screenId;
                    if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                            unusedScreens.contains(screenId)) {
                        unusedScreens.remove(screenId);
                    }
                }

                // If there are any empty screens remove them, and update.
                if (unusedScreens.size() != 0) {
                    sBgWorkspaceScreens.removeAll(unusedScreens);
                    updateWorkspaceScreenOrder(context, sBgWorkspaceScreens);
                }

                if (DEBUG_LOADERS) {
                    Log.d(TAG, "loaded workspace in " + (SystemClock.uptimeMillis() - t) + "ms");
                    Log.d(TAG, "workspace layout: ");
                    int nScreens = occupied.size();
                    for (int y = 0; y < countY; y++) {
                        String line = "";

                        for (int i = 0; i < nScreens; i++) {
                            long screenId = occupied.keyAt(i);
                            if (screenId > 0) {
                                line += " | ";
                            }
                            ItemInfo[][] screen = occupied.valueAt(i);
                            for (int x = 0; x < countX; x++) {
                                if (x < screen.length && y < screen[x].length) {
                                    line += (screen[x][y] != null) ? "#" : ".";
                                } else {
                                    line += "!";
                                }
                            }
                        }
                        Log.d(TAG, "[ " + line + " ]");
                    }
                }
            }
        }

        /**
         * Filters the set of items who are directly or indirectly (via another container) on the
         * specified screen.
         */
        private void filterCurrentWorkspaceItems(long currentScreenId,
                                                 ArrayList<ItemInfo> allWorkspaceItems,
                                                 ArrayList<ItemInfo> currentScreenItems,
                                                 ArrayList<ItemInfo> otherScreenItems) {
            // Purge any null ItemInfos
            Iterator<ItemInfo> iter = allWorkspaceItems.iterator();
            while (iter.hasNext()) {
                ItemInfo i = iter.next();
                if (i == null) {
                    iter.remove();
                }
            }

            // Order the set of items by their containers first, this allows use to walk through the
            // list sequentially, build up a list of containers that are in the specified screen,
            // as well as all items in those containers.
            Set<Long> itemsOnScreen = new HashSet<Long>();
            Collections.sort(allWorkspaceItems, new Comparator<ItemInfo>() {
                @Override
                public int compare(ItemInfo lhs, ItemInfo rhs) {
                    return (int) (lhs.container - rhs.container);
                }
            });
            for (ItemInfo info : allWorkspaceItems) {
                if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                    if (info.screenId == currentScreenId) {
                        currentScreenItems.add(info);
                        itemsOnScreen.add(info.id);
                    } else {
                        otherScreenItems.add(info);
                    }
                } else if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                    currentScreenItems.add(info);
                    itemsOnScreen.add(info.id);
                } else {
                    if (itemsOnScreen.contains(info.container)) {
                        currentScreenItems.add(info);
                        itemsOnScreen.add(info.id);
                    } else {
                        otherScreenItems.add(info);
                    }
                }
            }
        }

        /**
         * Filters the set of widgets which are on the specified screen.
         */
        private void filterCurrentAppWidgets(long currentScreenId,
                                             ArrayList<LauncherAppWidgetInfo> appWidgets,
                                             ArrayList<LauncherAppWidgetInfo> currentScreenWidgets,
                                             ArrayList<LauncherAppWidgetInfo> otherScreenWidgets) {

            for (LauncherAppWidgetInfo widget : appWidgets) {
                if (widget == null) continue;
                if (widget.container == LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                        widget.screenId == currentScreenId) {
                    currentScreenWidgets.add(widget);
                } else {
                    otherScreenWidgets.add(widget);
                }
            }
        }

        /**
         * Filters the set of folders which are on the specified screen.
         */
        private void filterCurrentFolders(long currentScreenId,
                                          LongArrayMap<ItemInfo> itemsIdMap,
                                          LongArrayMap<FolderInfo> folders,
                                          LongArrayMap<FolderInfo> currentScreenFolders,
                                          LongArrayMap<FolderInfo> otherScreenFolders) {

            int total = folders.size();
            for (int i = 0; i < total; i++) {
                long id = folders.keyAt(i);
                FolderInfo folder = folders.valueAt(i);

                ItemInfo info = itemsIdMap.get(id);
                if (info == null || folder == null) continue;
                if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                        info.screenId == currentScreenId) {
                    currentScreenFolders.put(id, folder);
                } else {
                    otherScreenFolders.put(id, folder);
                }
            }
        }

        /**
         * Sorts the set of items by hotseat, workspace (spatially from top to bottom, left to
         * right)
         */
        private void sortWorkspaceItemsSpatially(ArrayList<ItemInfo> workspaceItems) {
            final LauncherAppState app = LauncherAppState.getInstance();
            final InvariantDeviceProfile profile = app.getInvariantDeviceProfile();
            // XXX: review this
            Collections.sort(workspaceItems, new Comparator<ItemInfo>() {
                @Override
                public int compare(ItemInfo lhs, ItemInfo rhs) {
                    int cellCountX = (int) profile.numColumns;
                    int cellCountY = (int) profile.numRows;
                    int screenOffset = cellCountX * cellCountY;
                    int containerOffset = screenOffset * (Launcher.SCREEN_COUNT + 1); // +1 hotseat
                    long lr = (lhs.container * containerOffset + lhs.screenId * screenOffset +
                            lhs.cellY * cellCountX + lhs.cellX);
                    long rr = (rhs.container * containerOffset + rhs.screenId * screenOffset +
                            rhs.cellY * cellCountX + rhs.cellX);
                    return (int) (lr - rr);
                }
            });
        }

        private void bindWorkspaceScreens(final Callbacks oldCallbacks,
                                          final ArrayList<Long> orderedScreens) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.bindScreens(orderedScreens);
                    }
                }
            };
            runOnMainThread(r);
        }

        private void bindWorkspaceItems(final Callbacks oldCallbacks,
                                        final ArrayList<ItemInfo> workspaceItems,
                                        final ArrayList<LauncherAppWidgetInfo> appWidgets,
                                        final LongArrayMap<FolderInfo> folders,
                                        ArrayList<Runnable> deferredBindRunnables) {

            final boolean postOnMainThread = (deferredBindRunnables != null);

            // Bind the workspace items
            int N = workspaceItems.size();
            for (int i = 0; i < N; i += ITEMS_CHUNK) {
                final int start = i;
                final int chunkSize = (i + ITEMS_CHUNK <= N) ? ITEMS_CHUNK : (N - i);
                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            callbacks.bindItems(workspaceItems, start, start + chunkSize,
                                    false);
                        }
                    }
                };
                if (postOnMainThread) {
                    synchronized (deferredBindRunnables) {
                        deferredBindRunnables.add(r);
                    }
                } else {
                    runOnMainThread(r);
                }
            }

            // Bind the folders
            if (!folders.isEmpty()) {
                final Runnable r = new Runnable() {
                    public void run() {
                        Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            callbacks.bindFolders(folders);
                        }
                    }
                };
                if (postOnMainThread) {
                    synchronized (deferredBindRunnables) {
                        deferredBindRunnables.add(r);
                    }
                } else {
                    runOnMainThread(r);
                }
            }

            // Bind the widgets, one at a time
            N = appWidgets.size();
            for (int i = 0; i < N; i++) {
                final LauncherAppWidgetInfo widget = appWidgets.get(i);
                final Runnable r = new Runnable() {
                    public void run() {
                        Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            callbacks.bindAppWidget(widget);
                        }
                    }
                };
                if (postOnMainThread) {
                    deferredBindRunnables.add(r);
                } else {
                    runOnMainThread(r);
                }
            }
        }

        /**
         * Binds all loaded data to actual views on the main thread.
         */
        private void bindWorkspace(int synchronizeBindPage) {
            final long t = SystemClock.uptimeMillis();
            Runnable r;

            // Don't use these two variables in any of the callback runnables.
            // Otherwise we hold a reference to them.
            final Callbacks oldCallbacks = mCallbacks.get();
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher");
                return;
            }

            // Save a copy of all the bg-thread collections
            ArrayList<ItemInfo> workspaceItems = new ArrayList<ItemInfo>();
            ArrayList<LauncherAppWidgetInfo> appWidgets =
                    new ArrayList<LauncherAppWidgetInfo>();
            ArrayList<Long> orderedScreenIds = new ArrayList<Long>();

            final LongArrayMap<FolderInfo> folders;
            final LongArrayMap<ItemInfo> itemsIdMap;

            synchronized (sBgLock) {
                workspaceItems.addAll(sBgWorkspaceItems);
                appWidgets.addAll(sBgAppWidgets);
                orderedScreenIds.addAll(sBgWorkspaceScreens);

                folders = sBgFolders.clone();
                itemsIdMap = sBgItemsIdMap.clone();
            }

            final boolean isLoadingSynchronously =
                    synchronizeBindPage != PagedView.INVALID_RESTORE_PAGE;
            int currScreen = isLoadingSynchronously ? synchronizeBindPage :
                    oldCallbacks.getCurrentWorkspaceScreen();
            if (currScreen >= orderedScreenIds.size()) {
                // There may be no workspace screens (just hotseat items and an empty page).
                currScreen = PagedView.INVALID_RESTORE_PAGE;
            }
            final int currentScreen = currScreen;
            final long currentScreenId = currentScreen < 0
                    ? INVALID_SCREEN_ID : orderedScreenIds.get(currentScreen);

            // Load all the items that are on the current page first (and in the process, unbind
            // all the existing workspace items before we call startBinding() below.
            unbindWorkspaceItemsOnMainThread();

            // Separate the items that are on the current screen, and all the other remaining items
            ArrayList<ItemInfo> currentWorkspaceItems = new ArrayList<ItemInfo>();
            ArrayList<ItemInfo> otherWorkspaceItems = new ArrayList<ItemInfo>();
            ArrayList<LauncherAppWidgetInfo> currentAppWidgets =
                    new ArrayList<LauncherAppWidgetInfo>();
            ArrayList<LauncherAppWidgetInfo> otherAppWidgets =
                    new ArrayList<LauncherAppWidgetInfo>();
            LongArrayMap<FolderInfo> currentFolders = new LongArrayMap<>();
            LongArrayMap<FolderInfo> otherFolders = new LongArrayMap<>();

            filterCurrentWorkspaceItems(currentScreenId, workspaceItems, currentWorkspaceItems,
                    otherWorkspaceItems);
            filterCurrentAppWidgets(currentScreenId, appWidgets, currentAppWidgets,
                    otherAppWidgets);
            filterCurrentFolders(currentScreenId, itemsIdMap, folders, currentFolders,
                    otherFolders);
            sortWorkspaceItemsSpatially(currentWorkspaceItems);
            sortWorkspaceItemsSpatially(otherWorkspaceItems);

            // Tell the workspace that we're about to start binding items
            r = new Runnable() {
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.startBinding();
                    }
                }
            };
            runOnMainThread(r);

            bindWorkspaceScreens(oldCallbacks, orderedScreenIds);

            // Load items on the current page
            bindWorkspaceItems(oldCallbacks, currentWorkspaceItems, currentAppWidgets,
                    currentFolders, null);
            if (isLoadingSynchronously) {
                r = new Runnable() {
                    public void run() {
                        Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                        if (callbacks != null && currentScreen != PagedView.INVALID_RESTORE_PAGE) {
                            callbacks.onPageBoundSynchronously(currentScreen);
                        }
                    }
                };
                runOnMainThread(r);
            }

            // Load all the remaining pages (if we are loading synchronously, we want to defer this
            // work until after the first render)
            synchronized (mDeferredBindRunnables) {
                mDeferredBindRunnables.clear();
            }
            bindWorkspaceItems(oldCallbacks, otherWorkspaceItems, otherAppWidgets, otherFolders,
                    (isLoadingSynchronously ? mDeferredBindRunnables : null));

            // Tell the workspace that we're done binding items
            r = new Runnable() {
                public void run() {
                    Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.finishBindingItems();
                    }

                    mIsLoadingAndBindingWorkspace = false;

                    // Run all the bind complete runnables after workspace is bound.
                    if (!mBindCompleteRunnables.isEmpty()) {
                        synchronized (mBindCompleteRunnables) {
                            for (final Runnable r : mBindCompleteRunnables) {
                                runOnWorkerThread(r);
                            }
                            mBindCompleteRunnables.clear();
                        }
                    }

                    // If we're profiling, ensure this is the last thing in the queue.
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "bound workspace in "
                                + (SystemClock.uptimeMillis() - t) + "ms");
                    }

                }
            };
            if (isLoadingSynchronously) {
                synchronized (mDeferredBindRunnables) {
                    mDeferredBindRunnables.add(r);
                }
            } else {
                runOnMainThread(r);
            }

        }

        private void loadWebPresetApps() {
            if (BuildConfig.DEBUG) {
                PrevAppsShortcutPresent prevAppsShortcutPresent = new PrevAppsShortcutPresent();
                prevAppsShortcutPresent.init(LauncherModel.this);
                prevAppsShortcutPresent.process(new ArrayList<String>(), true, null);
            }
        }

        private void loadAndBindAllApps() {
            if (DEBUG_LOADERS) {
                Log.d(TAG, "loadAndBindAllApps mAllAppsLoaded=" + mAllAppsLoaded);
            }
            if (!mAllAppsLoaded) {
                loadAllApps();
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                }
                updateIconCache();
                synchronized (LoaderTask.this) {
                    if (mStopped) {
                        return;
                    }
                    mAllAppsLoaded = true;
                }
            } else {
                onlyBindAllApps();
            }
        }

        private void updateIconCache() {
            // Ignore packages which have a promise icon.
            HashSet<String> packagesToIgnore = new HashSet<>();
            synchronized (sBgLock) {
                for (ItemInfo info : sBgItemsIdMap) {
                    if (info instanceof ShortcutInfo) {
                        ShortcutInfo si = (ShortcutInfo) info;
                        if (si.isPromise() && si.getTargetComponent() != null) {
                            packagesToIgnore.add(si.getTargetComponent().getPackageName());
                        }
                    } else if (info instanceof LauncherAppWidgetInfo) {
                        LauncherAppWidgetInfo lawi = (LauncherAppWidgetInfo) info;
                        if (lawi.hasRestoreFlag(LauncherAppWidgetInfo.FLAG_PROVIDER_NOT_READY)) {
                            packagesToIgnore.add(lawi.providerName.getPackageName());
                        }
                    }
                }
            }
            mIconCache.updateDbIcons(packagesToIgnore);
        }

        private void onlyBindAllApps() {
            final Callbacks oldCallbacks = mCallbacks.get();
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher (onlyBindAllApps)");
                return;
            }

            // shallow copy
            @SuppressWarnings("unchecked") final ArrayList<AppInfo> list
                    = (ArrayList<AppInfo>) mBgAllAppsList.data.clone();
            final WidgetsModel widgetList = mBgWidgetsModel.clone();
            Runnable r = new Runnable() {
                public void run() {
                    final long t = SystemClock.uptimeMillis();
                    final Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.bindAllApplications(list);
                        callbacks.bindAllPackages(widgetList);
                    }
                    if (DEBUG_LOADERS) {
                        Log.d(TAG, "bound all " + list.size() + " apps from cache in "
                                + (SystemClock.uptimeMillis() - t) + "ms");
                    }
                }
            };
            boolean isRunningOnMainThread = !(sWorkerThread.getThreadId() == Process.myTid());
            if (isRunningOnMainThread) {
                r.run();
            } else {
                mHandler.post(r);
            }
        }

        private void loadAllApps() {
            final long loadTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;

            final Callbacks oldCallbacks = mCallbacks.get();
            if (oldCallbacks == null) {
                // This launcher has exited and nobody bothered to tell us.  Just bail.
                Log.w(TAG, "LoaderTask running with no launcher (loadAllApps)");
                return;
            }

            final List<UserHandleCompat> profiles = mUserManager.getUserProfiles();

            // Clear the list of apps
            mBgAllAppsList.clear();
            for (UserHandleCompat user : profiles) {
                // Query for the set of apps
                final long qiaTime = DEBUG_LOADERS ? SystemClock.uptimeMillis() : 0;
                final List<LauncherActivityInfoCompat> apps = mLauncherApps.getActivityList(null, user);
                if (DEBUG_LOADERS) {
                    Log.d(TAG, "getActivityList took "
                            + (SystemClock.uptimeMillis() - qiaTime) + "ms for user " + user);
                    Log.d(TAG, "getActivityList got " + apps.size() + " apps for user " + user);
                }
                // Fail if we don't have any apps
                // TODO: Fix this. Only fail for the current user.
                if (apps == null || apps.isEmpty()) {
                    return;
                }

                // Create the ApplicationInfos
                for (int i = 0; i < apps.size(); i++) {
                    LauncherActivityInfoCompat app = apps.get(i);
                    // This builds the icon bitmaps.
                    mBgAllAppsList.add(new AppInfo(mContext, app, user, mIconCache));
                }

                final ManagedProfileHeuristic heuristic = ManagedProfileHeuristic.get(mContext, user);
                if (heuristic != null) {
                    final Runnable r = new Runnable() {

                        @Override
                        public void run() {
                            heuristic.processUserApps(apps);
                        }
                    };
                    runOnMainThread(new Runnable() {

                        @Override
                        public void run() {
                            // Check isLoadingWorkspace on the UI thread, as it is updated on
                            // the UI thread.
                            if (mIsLoadingAndBindingWorkspace) {
                                synchronized (mBindCompleteRunnables) {
                                    mBindCompleteRunnables.add(r);
                                }
                            } else {
                                runOnWorkerThread(r);
                            }
                        }
                    });
                }
            }
            // Huh? Shouldn't this be inside the Runnable below?
            final ArrayList<AppInfo> added = mBgAllAppsList.added;
            mBgAllAppsList.added = new ArrayList<AppInfo>();

            // Post callback on main thread
            mHandler.post(new Runnable() {
                public void run() {

                    final long bindTime = SystemClock.uptimeMillis();
                    final Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.bindAllApplications(added);
                        if (DEBUG_LOADERS) {
                            Log.d(TAG, "bound " + added.size() + " apps in "
                                    + (SystemClock.uptimeMillis() - bindTime) + "ms");
                        }
                    } else {
                        Log.i(TAG, "not binding apps: no Launcher activity");
                    }
                }
            });
            // Cleanup any data stored for a deleted user.
            ManagedProfileHeuristic.processAllUsers(profiles, mContext);

            loadAndBindWidgetsAndShortcuts(tryGetCallbacks(oldCallbacks), true /* refresh */);
            if (DEBUG_LOADERS) {
                Log.d(TAG, "Icons processed in "
                        + (SystemClock.uptimeMillis() - loadTime) + "ms");
            }
        }

        public void dumpState() {
            synchronized (sBgLock) {
                Log.d(TAG, "mLoaderTask.mContext=" + mContext);
                Log.d(TAG, "mLoaderTask.mStopped=" + mStopped);
                Log.d(TAG, "mLoaderTask.mLoadAndBindStepFinished=" + mLoadAndBindStepFinished);
                Log.d(TAG, "mItems size=" + sBgWorkspaceItems.size());
            }
        }
    }

    // check & update map of what's occupied; used to discard overlapping/invalid items
    private boolean checkItemPlacement(LongArrayMap<ItemInfo[][]> occupied, ItemInfo item,
                                       ArrayList<Long> workspaceScreens) {
        LauncherAppState app = LauncherAppState.getInstance();
        InvariantDeviceProfile profile = app.getInvariantDeviceProfile();
        final int countX = profile.numColumns;
        final int countY = profile.numRows;

        long containerIndex = item.screenId;
        if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            // Return early if we detect that an item is under the hotseat button
            //这里原来判断AllAppsButton要注释掉,目前需求要去掉AllAppsButton(否则预置图标添加不到这个位置)
//                if (mCallbacks == null ||
//                        mCallbacks.get().isAllAppsButtonRank((int) item.screenId)) {
//                    Log.e(TAG, "Error loading shortcut into hotseat " + item
//                            + " into position (" + item.screenId + ":" + item.cellX + ","
//                            + item.cellY + ") occupied by all apps");
//                    return false;
//                }

            final ItemInfo[][] hotseatItems =
                    occupied.get((long) LauncherSettings.Favorites.CONTAINER_HOTSEAT);

            if (item.screenId >= profile.numHotseatIcons) {
                Log.e(TAG, "Error loading shortcut " + item
                        + " into hotseat position " + item.screenId
                        + ", position out of bounds: (0 to " + (profile.numHotseatIcons - 1)
                        + ")");
                return false;
            }

            if (hotseatItems != null) {
                if (hotseatItems[(int) item.screenId][0] != null) {
                    Log.e(TAG, "Error loading shortcut into hotseat " + item
                            + " into position (" + item.screenId + ":" + item.cellX + ","
                            + item.cellY + ") occupied by "
                            + occupied.get(LauncherSettings.Favorites.CONTAINER_HOTSEAT)
                            [(int) item.screenId][0]);
                    return false;
                } else {
                    hotseatItems[(int) item.screenId][0] = item;
                    return true;
                }
            } else {
                final ItemInfo[][] items = new ItemInfo[(int) profile.numHotseatIcons][1];
                items[(int) item.screenId][0] = item;
                occupied.put((long) LauncherSettings.Favorites.CONTAINER_HOTSEAT, items);
                return true;
            }
        } else if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            if (!workspaceScreens.contains((Long) item.screenId)) {
                // The item has an invalid screen id.
                return false;
            }
        } else {
            // Skip further checking if it is not the hotseat or workspace container
            return true;
        }

        //初始化屏幕
        if (!occupied.containsKey(item.screenId)) {
            ItemInfo[][] items = new ItemInfo[countX + 1][countY + 1];
            occupied.put(item.screenId, items);
        }

        //取得对应的屏幕
        final ItemInfo[][] screens = occupied.get(item.screenId);
        if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                item.cellX < 0 || item.cellY < 0 ||
                item.cellX + item.spanX > countX || item.cellY + item.spanY > countY) {
            Log.e(TAG, "Error loading shortcut " + item
                    + " into cell (" + containerIndex + "-" + item.screenId + ":"
                    + item.cellX + "," + item.cellY
                    + ") out of screen bounds ( " + countX + "x" + countY + ")");
            return false;
        }

        // Check if any workspace icons overlap with each other
        // 检查图标位置是否重叠，后面的那个会被移除
        for (int x = item.cellX; x < (item.cellX + item.spanX); x++) {
            for (int y = item.cellY; y < (item.cellY + item.spanY); y++) {
                if (screens[x][y] != null) {
                    Log.e(TAG, "Error loading shortcut " + item
                            + " into cell (" + containerIndex + "-" + item.screenId + ":"
                            + x + "," + y
                            + ") occupied by "
                            + screens[x][y]);
                    return false;
                }
            }
        }
        //这里图标在屏幕上的位置被确定
        for (int x = item.cellX; x < (item.cellX + item.spanX); x++) {
            for (int y = item.cellY; y < (item.cellY + item.spanY); y++) {
                screens[x][y] = item;
            }
        }

        return true;
    }


    /**
     * Clears all the sBg data structures
     */
    private void clearSBgDataStructures() {
        synchronized (sBgLock) {
            sBgWorkspaceItems.clear();
            sBgAppWidgets.clear();
            sBgFolders.clear();
            sBgItemsIdMap.clear();
            sBgWorkspaceScreens.clear();
            sPresetItemsIdList.clear();
        }
    }

    /**
     * Partially updates the item without any notification. Must be called on the worker thread.
     */
    private void updateItem(long itemId, ContentValues update) {
        Context context = LauncherAppState.getInstance().getContext();
        context.getContentResolver().update(
                LauncherSettings.Favorites.CONTENT_URI,
                update,
                BaseColumns._ID + "= ?",
                new String[]{Long.toString(itemId)});
    }

    /**
     * Called when the icons for packages have been updated in the icon cache.
     */
    public void onPackageIconsUpdated(HashSet<String> updatedPackages, UserHandleCompat user) {
        final Callbacks callbacks = getCallback();
        final ArrayList<AppInfo> updatedApps = new ArrayList<>();
        final ArrayList<ShortcutInfo> updatedShortcuts = new ArrayList<>();

        // If any package icon has changed (app was updated while launcher was dead),
        // update the corresponding shortcuts.
        synchronized (sBgLock) {
            for (ItemInfo info : sBgItemsIdMap) {
                if (info instanceof ShortcutInfo && user.equals(info.user)
                        && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
                    ShortcutInfo si = (ShortcutInfo) info;
                    ComponentName cn = si.getTargetComponent();
                    if (cn != null && updatedPackages.contains(cn.getPackageName())) {
                        si.updateIcon(mIconCache);
                        updatedShortcuts.add(si);
                    }
                }
            }
            mBgAllAppsList.updateIconsAndLabels(updatedPackages, user, updatedApps);
        }

        if (!updatedShortcuts.isEmpty()) {
            final UserHandleCompat userFinal = user;
            mHandler.post(new Runnable() {

                public void run() {
                    Callbacks cb = getCallback();
                    if (cb != null && callbacks == cb) {
                        cb.bindShortcutsChanged(updatedShortcuts,
                                new ArrayList<ShortcutInfo>(), userFinal);
                    }
                }
            });
        }

        if (!updatedApps.isEmpty()) {
            mHandler.post(new Runnable() {

                public void run() {
                    Callbacks cb = getCallback();
                    if (cb != null && callbacks == cb) {
                        cb.bindAppsUpdated(updatedApps);
                    }
                }
            });
        }

        // Reload widget list. No need to refresh, as we only want to update the icons and labels.
        loadAndBindWidgetsAndShortcuts(callbacks, false);
    }

    void enqueuePackageUpdated(PackageUpdatedTask task) {
        sWorker.post(task);
    }

    @Thunk
    class AppsAvailabilityCheck extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (sBgLock) {
                final LauncherAppsCompat launcherApps = LauncherAppsCompat
                        .getInstance(mApp.getContext());
                final PackageManager manager = context.getPackageManager();
                final ArrayList<String> packagesRemoved = new ArrayList<String>();
                final ArrayList<String> packagesUnavailable = new ArrayList<String>();
                for (Entry<UserHandleCompat, HashSet<String>> entry : sPendingPackages.entrySet()) {
                    UserHandleCompat user = entry.getKey();
                    packagesRemoved.clear();
                    packagesUnavailable.clear();
                    for (String pkg : entry.getValue()) {
                        //不检查预置图标
                        synchronized (sBgLock) {
                            ShortcutInfo itemFromSbItemsByPackageName = getItemFromSbItemsByPackageName(pkg);
                            if (itemFromSbItemsByPackageName != null &&
                                    itemFromSbItemsByPackageName.itemType == LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                                continue;
                            }
                        }

                        if (!launcherApps.isPackageEnabledForProfile(pkg, user)) {
                            boolean packageOnSdcard = launcherApps.isAppEnabled(
                                    manager, pkg, PackageManager.GET_UNINSTALLED_PACKAGES);
                            if (packageOnSdcard) {
                                Launcher.addDumpLog(TAG, "Package found on sd-card: " + pkg, true);
                                packagesUnavailable.add(pkg);
                            } else {
                                Launcher.addDumpLog(TAG, "Package not found: " + pkg, true);
                                packagesRemoved.add(pkg);
                            }
                        }
                    }
                    if (!packagesRemoved.isEmpty()) {
                        enqueuePackageUpdated(new PackageUpdatedTask(PackageUpdatedTask.OP_REMOVE,
                                packagesRemoved.toArray(new String[packagesRemoved.size()]), user));
                    }
                    if (!packagesUnavailable.isEmpty()) {
                        enqueuePackageUpdated(new PackageUpdatedTask(PackageUpdatedTask.OP_UNAVAILABLE,
                                packagesUnavailable.toArray(new String[packagesUnavailable.size()]), user));
                    }
                }
                sPendingPackages.clear();
            }
        }
    }

    private class PackageUpdatedTask implements Runnable {
        int mOp;
        String[] mPackages;
        UserHandleCompat mUser;

        public static final int OP_NONE = 0;
        public static final int OP_ADD = 1;
        public static final int OP_UPDATE = 2;
        public static final int OP_REMOVE = 3; // uninstlled
        public static final int OP_UNAVAILABLE = 4; // external media unmounted


        public PackageUpdatedTask(int op, String[] packages, UserHandleCompat user) {
            mOp = op;
            mPackages = packages;
            mUser = user;
        }

        public void run() {
            if (!mHasLoaderCompletedOnce) {
                // Loader has not yet run.
                return;
            }
            final Context context = mApp.getContext();

            final String[] packages = mPackages;
            final int N = packages.length;
            switch (mOp) {
                case OP_ADD: {
                    for (int i = 0; i < N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.addPackage " + packages[i]);
                        mIconCache.updateIconsForPkg(packages[i], mUser);
                        mBgAllAppsList.addPackage(context, packages[i], mUser);
                    }

                    ManagedProfileHeuristic heuristic = ManagedProfileHeuristic.get(context, mUser);
                    if (heuristic != null) {
                        heuristic.processPackageAdd(mPackages);
                    }
                    break;
                }
                case OP_UPDATE:
                    for (int i = 0; i < N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.updatePackage " + packages[i]);
                        mIconCache.updateIconsForPkg(packages[i], mUser);
                        mBgAllAppsList.updatePackage(context, packages[i], mUser);
                        mApp.getWidgetCache().removePackage(packages[i], mUser);
                    }
                    break;
                case OP_REMOVE: {
                    ManagedProfileHeuristic heuristic = ManagedProfileHeuristic.get(context, mUser);
                    if (heuristic != null) {
                        heuristic.processPackageRemoved(mPackages);
                    }
                    for (int i = 0; i < N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.removePackage " + packages[i]);
                        mIconCache.removeIconsForPkg(packages[i], mUser);
                    }
                    // Fall through
                }
                case OP_UNAVAILABLE:
                    for (int i = 0; i < N; i++) {
                        if (DEBUG_LOADERS) Log.d(TAG, "mAllAppsList.removePackage " + packages[i]);
                        mBgAllAppsList.removePackage(packages[i], mUser);
                        mApp.getWidgetCache().removePackage(packages[i], mUser);
                    }
                    break;
            }

            ArrayList<AppInfo> added = null;
            ArrayList<AppInfo> modified = null;
            final ArrayList<AppInfo> removedApps = new ArrayList<AppInfo>();

            if (mBgAllAppsList.added.size() > 0) {
                added = new ArrayList<AppInfo>(mBgAllAppsList.added);
                mBgAllAppsList.added.clear();
            }
            if (mBgAllAppsList.modified.size() > 0) {
                modified = new ArrayList<AppInfo>(mBgAllAppsList.modified);
                mBgAllAppsList.modified.clear();
            }
            if (mBgAllAppsList.removed.size() > 0) {
                removedApps.addAll(mBgAllAppsList.removed);
                mBgAllAppsList.removed.clear();
            }

            final Callbacks callbacks = getCallback();
            if (callbacks == null) {
                Log.w(TAG, "Nobody to tell about the new app.  Launcher is probably loading.");
                return;
            }

            final HashMap<ComponentName, AppInfo> addedOrUpdatedApps =
                    new HashMap<ComponentName, AppInfo>();

            if (added != null) {
                //1.安装应用的时候，在桌边创建快捷方式
                final ArrayList<ItemInfo> addedInfos = new ArrayList<ItemInfo>(added);
                Log.e(TAG, "PackageUpdatedTask ADD:" + addedInfos);
                addAndBindAddedWorkspaceItems(context, addedInfos);

                addAppsToAllApps(context, added);
                for (AppInfo ai : added) {
                    addedOrUpdatedApps.put(ai.componentName, ai);
                }
            }

            if (modified != null) {
                final ArrayList<AppInfo> modifiedFinal = modified;
                for (AppInfo ai : modified) {
                    addedOrUpdatedApps.put(ai.componentName, ai);
                }

                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = getCallback();
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsUpdated(modifiedFinal);
                        }
                    }
                });
            }

            // Update shortcut infos
            if (mOp == OP_ADD || mOp == OP_UPDATE) {
                final ArrayList<ShortcutInfo> updatedShortcuts = new ArrayList<ShortcutInfo>();
                final ArrayList<ShortcutInfo> removedShortcuts = new ArrayList<ShortcutInfo>();
                final ArrayList<LauncherAppWidgetInfo> widgets = new ArrayList<LauncherAppWidgetInfo>();

                HashSet<String> packageSet = new HashSet<String>(Arrays.asList(packages));
                synchronized (sBgLock) {
                    for (ItemInfo info : sBgItemsIdMap) {
                        if (info instanceof ShortcutInfo && mUser.equals(info.user)) {
                            ShortcutInfo si = (ShortcutInfo) info;
                            boolean infoUpdated = false;
                            boolean shortcutUpdated = false;

                            // Update shortcuts which use iconResource.
                            if ((si.iconResource != null)
                                    && packageSet.contains(si.iconResource.packageName)) {
                                Bitmap icon = Utilities.createIconBitmap(
                                        si.iconResource.packageName,
                                        si.iconResource.resourceName, context);
                                if (icon != null) {
                                    si.setIcon(icon);
                                    si.usingFallbackIcon = false;
                                    infoUpdated = true;
                                }
                            }

                            ComponentName cn = si.getTargetComponent();
                            if (cn != null && packageSet.contains(cn.getPackageName())) {
                                AppInfo appInfo = addedOrUpdatedApps.get(cn);

                                if (si.isPromise()) {
                                    if (si.hasStatusFlag(ShortcutInfo.FLAG_AUTOINTALL_ICON)) {
                                        // Auto install icon
                                        PackageManager pm = context.getPackageManager();
                                        ResolveInfo matched = pm.resolveActivity(
                                                new Intent(Intent.ACTION_MAIN)
                                                        .setComponent(cn).addCategory(Intent.CATEGORY_LAUNCHER),
                                                PackageManager.MATCH_DEFAULT_ONLY);
                                        if (matched == null) {
                                            // Try to find the best match activity.
                                            Intent intent = pm.getLaunchIntentForPackage(
                                                    cn.getPackageName());
                                            if (intent != null) {
                                                cn = intent.getComponent();
                                                appInfo = addedOrUpdatedApps.get(cn);
                                            }

                                            if ((intent == null) || (appInfo == null)) {
                                                removedShortcuts.add(si);
                                                continue;
                                            }
                                            si.promisedIntent = intent;
                                        }
                                    }

                                    // Restore the shortcut.
                                    if (appInfo != null) {
                                        si.flags = appInfo.flags;
                                    }

                                    si.intent = si.promisedIntent;
                                    si.promisedIntent = null;
                                    si.status = ShortcutInfo.DEFAULT;
                                    infoUpdated = true;
                                    si.updateIcon(mIconCache);
                                }

                                if (appInfo != null && Intent.ACTION_MAIN.equals(si.intent.getAction())
                                        && si.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
                                    si.updateIcon(mIconCache);
                                    si.title = Utilities.trim(appInfo.title);
                                    si.contentDescription = appInfo.contentDescription;
                                    infoUpdated = true;
                                }

                                if ((si.isDisabled & ShortcutInfo.FLAG_DISABLED_NOT_AVAILABLE) != 0) {
                                    // Since package was just updated, the target must be available now.
                                    si.isDisabled &= ~ShortcutInfo.FLAG_DISABLED_NOT_AVAILABLE;
                                    shortcutUpdated = true;
                                }
                            }

                            if (infoUpdated || shortcutUpdated) {
                                updatedShortcuts.add(si);
                            }
                            if (infoUpdated) {
                                updateItemInDatabase(context, si);
                            }
                        } else if (info instanceof LauncherAppWidgetInfo) {
                            LauncherAppWidgetInfo widgetInfo = (LauncherAppWidgetInfo) info;
                            if (mUser.equals(widgetInfo.user)
                                    && widgetInfo.hasRestoreFlag(LauncherAppWidgetInfo.FLAG_PROVIDER_NOT_READY)
                                    && packageSet.contains(widgetInfo.providerName.getPackageName())) {
                                widgetInfo.restoreStatus &=
                                        ~LauncherAppWidgetInfo.FLAG_PROVIDER_NOT_READY &
                                                ~LauncherAppWidgetInfo.FLAG_RESTORE_STARTED;

                                // adding this flag ensures that launcher shows 'click to setup'
                                // if the widget has a config activity. In case there is no config
                                // activity, it will be marked as 'restored' during bind.
                                widgetInfo.restoreStatus |= LauncherAppWidgetInfo.FLAG_UI_NOT_READY;

                                widgets.add(widgetInfo);
                                updateItemInDatabase(context, widgetInfo);
                            }
                        }
                    }
                }

                if (!updatedShortcuts.isEmpty() || !removedShortcuts.isEmpty()) {
                    mHandler.post(new Runnable() {

                        public void run() {
                            Callbacks cb = getCallback();
                            if (callbacks == cb && cb != null) {
                                callbacks.bindShortcutsChanged(
                                        updatedShortcuts, removedShortcuts, mUser);
                            }
                        }
                    });
                    if (!removedShortcuts.isEmpty()) {
                        deleteItemsFromDatabase(context, removedShortcuts);
                    }
                }
                if (!widgets.isEmpty()) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            Callbacks cb = getCallback();
                            if (callbacks == cb && cb != null) {
                                callbacks.bindWidgetsRestored(widgets);
                            }
                        }
                    });
                }
            }

            final ArrayList<String> removedPackageNames =
                    new ArrayList<String>();
            if (mOp == OP_REMOVE || mOp == OP_UNAVAILABLE) {
                // Mark all packages in the broadcast to be removed
                removedPackageNames.addAll(Arrays.asList(packages));
            } else if (mOp == OP_UPDATE) {
                // Mark disabled packages in the broadcast to be removed
                for (int i = 0; i < N; i++) {
                    if (isPackageDisabled(context, packages[i], mUser)) {
                        removedPackageNames.add(packages[i]);
                    }
                }
            }

            if (!removedPackageNames.isEmpty() || !removedApps.isEmpty()) {
                final int removeReason;
                if (mOp == OP_UNAVAILABLE) {
                    removeReason = ShortcutInfo.FLAG_DISABLED_NOT_AVAILABLE;
                } else {
                    // Remove all the components associated with this package
                    for (String pn : removedPackageNames) {
                        deletePackageFromDatabase(context, pn, mUser);
                    }
                    // Remove all the specific components
                    for (AppInfo a : removedApps) {
                        ArrayList<ItemInfo> infos = getItemInfoForComponentName(a.componentName, mUser);
                        deleteItemsFromDatabase(context, infos);
                    }
                    removeReason = 0;
                }

                // Remove any queued items from the install queue
                InstallShortcutReceiver.removeFromInstallQueue(context, removedPackageNames, mUser);
                // Call the components-removed callback
                mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = getCallback();
                        if (callbacks == cb && cb != null) {
                            callbacks.bindComponentsRemoved(
                                    removedPackageNames, removedApps, mUser, removeReason);
                        }
                    }
                });
            }

            // Update widgets
            if (mOp == OP_ADD || mOp == OP_REMOVE || mOp == OP_UPDATE) {
                // Always refresh for a package event on secondary user
                boolean needToRefresh = !mUser.equals(UserHandleCompat.myUserHandle());

                // Refresh widget list, if the package already had a widget.
                synchronized (sBgLock) {
                    if (sBgWidgetProviders != null) {
                        HashSet<String> pkgSet = new HashSet<>();
                        Collections.addAll(pkgSet, mPackages);

                        for (ComponentKey key : sBgWidgetProviders.keySet()) {
                            needToRefresh |= key.user.equals(mUser) &&
                                    pkgSet.contains(key.componentName.getPackageName());
                        }
                    }
                }

                if (!needToRefresh && mOp != OP_REMOVE) {
                    // Refresh widget list, if there is any newly added widget
                    PackageManager pm = context.getPackageManager();
                    for (String pkg : mPackages) {
                        needToRefresh |= !pm.queryBroadcastReceivers(
                                new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                                        .setPackage(pkg), 0).isEmpty();
                    }
                }

                loadAndBindWidgetsAndShortcuts(callbacks, needToRefresh);
            }

            // Write all the logs to disk
            mHandler.post(new Runnable() {
                public void run() {
                    Callbacks cb = getCallback();
                    if (callbacks == cb && cb != null) {
                        callbacks.dumpLogsToLocalData();
                    }
                }
            });
        }
    }

    public static List<LauncherAppWidgetProviderInfo> getWidgetProviders(Context context,
                                                                         boolean refresh) {
        ArrayList<LauncherAppWidgetProviderInfo> results =
                new ArrayList<LauncherAppWidgetProviderInfo>();
        try {
            synchronized (sBgLock) {
                if (sBgWidgetProviders == null || refresh) {
                    HashMap<ComponentKey, LauncherAppWidgetProviderInfo> tmpWidgetProviders
                            = new HashMap<>();
                    AppWidgetManagerCompat wm = AppWidgetManagerCompat.getInstance(context);
                    LauncherAppWidgetProviderInfo info;

                    List<AppWidgetProviderInfo> widgets = wm.getAllProviders();
                    for (AppWidgetProviderInfo pInfo : widgets) {
                        info = LauncherAppWidgetProviderInfo.fromProviderInfo(context, pInfo);
                        UserHandleCompat user = wm.getUser(info);
                        tmpWidgetProviders.put(new ComponentKey(info.provider, user), info);
                    }

                    Collection<CustomAppWidget> customWidgets = Launcher.getCustomAppWidgets().values();
                    for (CustomAppWidget widget : customWidgets) {
                        info = new LauncherAppWidgetProviderInfo(context, widget);
                        UserHandleCompat user = wm.getUser(info);
                        tmpWidgetProviders.put(new ComponentKey(info.provider, user), info);
                    }
                    // Replace the global list at the very end, so that if there is an exception,
                    // previously loaded provider list is used.
                    sBgWidgetProviders = tmpWidgetProviders;
                }
                results.addAll(sBgWidgetProviders.values());
                return results;
            }
        } catch (Exception e) {
            if (e.getCause() instanceof TransactionTooLargeException) {
                // the returned value may be incomplete and will not be refreshed until the next
                // time Launcher starts.
                // TODO: after figuring out a repro step, introduce a dirty bit to check when
                // onResume is called to refresh the widget provider list.
                synchronized (sBgLock) {
                    if (sBgWidgetProviders != null) {
                        results.addAll(sBgWidgetProviders.values());
                    }
                    return results;
                }
            } else {
                throw e;
            }
        }
    }

    public static LauncherAppWidgetProviderInfo getProviderInfo(Context ctx, ComponentName name,
                                                                UserHandleCompat user) {
        synchronized (sBgLock) {
            if (sBgWidgetProviders == null) {
                getWidgetProviders(ctx, false /* refresh */);
            }
            return sBgWidgetProviders.get(new ComponentKey(name, user));
        }
    }

    public void loadAndBindWidgetsAndShortcuts(final Callbacks callbacks, final boolean refresh) {

        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                updateWidgetsModel(refresh);
                final WidgetsModel model = mBgWidgetsModel.clone();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Callbacks cb = getCallback();
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAllPackages(model);
                        }
                    }
                });
                // update the Widget entries inside DB on the worker thread.
                LauncherAppState.getInstance().getWidgetCache().removeObsoletePreviews(
                        model.getRawList());
            }
        });
    }

    /**
     * Returns a list of ResolveInfos/AppWidgetInfos.
     *
     * @see #loadAndBindWidgetsAndShortcuts
     */
    @Thunk
    void updateWidgetsModel(boolean refresh) {
        PackageManager packageManager = mApp.getContext().getPackageManager();
        final ArrayList<Object> widgetsAndShortcuts = new ArrayList<Object>();
        widgetsAndShortcuts.addAll(getWidgetProviders(mApp.getContext(), refresh));
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        widgetsAndShortcuts.addAll(packageManager.queryIntentActivities(shortcutsIntent, 0));
        mBgWidgetsModel.setWidgetsAndShortcuts(widgetsAndShortcuts);
    }

    @Thunk
    static boolean isPackageDisabled(Context context, String packageName,
                                     UserHandleCompat user) {
        final LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(context);
        return !launcherApps.isPackageEnabledForProfile(packageName, user);
    }

    public static boolean isValidPackageActivity(Context context, ComponentName cn,
                                                 UserHandleCompat user) {
        if (cn == null) {
            return false;
        }
        final LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(context);
        if (!launcherApps.isPackageEnabledForProfile(cn.getPackageName(), user)) {
            return false;
        }
        return launcherApps.isActivityEnabledForProfile(cn, user);
    }

    public static boolean isValidPackage(Context context, String packageName,
                                         UserHandleCompat user) {
        if (packageName == null) {
            return false;
        }
        final LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(context);
        return launcherApps.isPackageEnabledForProfile(packageName, user);
    }

    /**
     * Make an ShortcutInfo object for a restored application or shortcut item that points
     * to a package that is not yet installed on the system.
     */
    public ShortcutInfo getRestoredItemInfo(Cursor c, int titleIndex, Intent intent,
                                            int promiseType, int itemType, CursorIconInfo iconInfo, Context context) {
        final ShortcutInfo info = new ShortcutInfo();
        info.user = UserHandleCompat.myUserHandle();

        Bitmap icon = iconInfo.loadIcon(c, info, context);
        // the fallback icon
        if (icon == null) {
            mIconCache.getTitleAndIcon(info, intent, info.user, false /* useLowResIcon */);
        } else {
            info.setIcon(icon);
        }

        if ((promiseType & ShortcutInfo.FLAG_RESTORED_ICON) != 0) {
            String title = (c != null) ? c.getString(titleIndex) : null;
            if (!TextUtils.isEmpty(title)) {
                info.title = Utilities.trim(title);
            }
        } else if ((promiseType & ShortcutInfo.FLAG_AUTOINTALL_ICON) != 0) {
            if (TextUtils.isEmpty(info.title)) {
                info.title = (c != null) ? Utilities.trim(c.getString(titleIndex)) : "";
            }
        } else {
            throw new InvalidParameterException("Invalid restoreType " + promiseType);
        }

        info.contentDescription = mUserManager.getBadgedLabelForUser(info.title, info.user);
        info.itemType = itemType;
        info.promisedIntent = intent;
        info.status = promiseType;
        return info;
    }

    /**
     * Make an Intent object for a restored application or shortcut item that points
     * to the market page for the item.
     */
    @Thunk
    Intent getRestoredItemIntent(Cursor c, Context context, Intent intent) {
        ComponentName componentName = intent.getComponent();
        return getMarketIntent(componentName.getPackageName());
    }

    static Intent getMarketIntent(String packageName) {
        return new Intent(Intent.ACTION_VIEW)
                .setData(new Uri.Builder()
                        .scheme("market")
                        .authority("details")
                        .appendQueryParameter("id", packageName)
                        .build());
    }

    /**
     * Make an ShortcutInfo object for a shortcut that is an application.
     * <p>
     * If c is not null, then it will be used to fill in missing data like the title and icon.
     */
    public ShortcutInfo getAppShortcutInfo(PackageManager manager, Intent intent,
                                           UserHandleCompat user, Context context, Cursor c, int iconIndex, int titleIndex,
                                           boolean allowMissingTarget, boolean useLowResIcon) {
        if (user == null) {
            Log.d(TAG, "Null user found in getShortcutInfo");
            return null;
        }

        ComponentName componentName = intent.getComponent();
        if (componentName == null) {
            Log.d(TAG, "Missing component found in getShortcutInfo: " + componentName);
            return null;
        }

        Intent newIntent = new Intent(intent.getAction(), null);
        newIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        newIntent.setComponent(componentName);
        LauncherActivityInfoCompat lai = mLauncherApps.resolveActivity(newIntent, user);
        if ((lai == null) && !allowMissingTarget) {
            Log.d(TAG, "Missing activity found in getShortcutInfo: " + componentName);
            return null;
        }

        final ShortcutInfo info = new ShortcutInfo();
        mIconCache.getTitleAndIcon(info, componentName, lai, user, false, useLowResIcon);
        if (mIconCache.isDefaultIcon(info.getIcon(mIconCache), user) && c != null) {
            Bitmap icon = Utilities.createIconBitmap(c, iconIndex, context);
            info.setIcon(icon == null ? mIconCache.getDefaultIcon(user) : icon);
        }

        // from the db
        if (TextUtils.isEmpty(info.title) && c != null) {
            info.title = Utilities.trim(c.getString(titleIndex));
        }

        // fall back to the class name of the activity
        if (info.title == null) {
            info.title = componentName.getClassName();
        }

        info.itemType = LauncherSettings.Favorites.ITEM_TYPE_APPLICATION;
        info.user = user;
        info.contentDescription = mUserManager.getBadgedLabelForUser(info.title, info.user);
        if (lai != null) {
            info.flags = AppInfo.initFlags(lai);
        }
        return info;
    }

    static ArrayList<ItemInfo> filterItemInfos(Iterable<ItemInfo> infos,
                                               ItemInfoFilter f) {
        HashSet<ItemInfo> filtered = new HashSet<ItemInfo>();
        for (ItemInfo i : infos) {
            if (i instanceof ShortcutInfo) {
                ShortcutInfo info = (ShortcutInfo) i;
                ComponentName cn = info.getTargetComponent();
                if (cn != null && f.filterItem(null, info, cn)) {
                    filtered.add(info);
                }
            } else if (i instanceof FolderInfo) {
                FolderInfo info = (FolderInfo) i;
                for (ShortcutInfo s : info.contents) {
                    ComponentName cn = s.getTargetComponent();
                    if (cn != null && f.filterItem(info, s, cn)) {
                        filtered.add(s);
                    }
                }
            } else if (i instanceof LauncherAppWidgetInfo) {
                LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) i;
                ComponentName cn = info.providerName;
                if (cn != null && f.filterItem(null, info, cn)) {
                    filtered.add(info);
                }
            }
        }
        return new ArrayList<ItemInfo>(filtered);
    }

    @Thunk
    ArrayList<ItemInfo> getItemInfoForComponentName(final ComponentName cname,
                                                    final UserHandleCompat user) {
        ItemInfoFilter filter = new ItemInfoFilter() {
            @Override
            public boolean filterItem(ItemInfo parent, ItemInfo info, ComponentName cn) {
                if (info.user == null) {
                    return cn.equals(cname);
                } else {
                    return cn.equals(cname) && info.user.equals(user);
                }
            }
        };
        return filterItemInfos(sBgItemsIdMap, filter);
    }

    @Thunk
    ShortcutInfo getPresetShortcutInfo(Cursor c, Context context,
                                       int titleIndex, CursorIconInfo iconInfo) {
        final ShortcutInfo info = getShortcutInfo(c, context, titleIndex, iconInfo);
        info.itemType = LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT;
        return info;
    }

    /**
     * Make an ShortcutInfo object for a shortcut that isn't an application.
     */
    @Thunk
    ShortcutInfo getShortcutInfo(Cursor c, Context context,
                                 int titleIndex, CursorIconInfo iconInfo) {
        final ShortcutInfo info = new ShortcutInfo();
        // Non-app shortcuts are only supported for current user.
        info.user = UserHandleCompat.myUserHandle();
        info.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;

        // TODO: If there's an explicit component and we can't install that, delete it.

        info.title = Utilities.trim(c.getString(titleIndex));

        Bitmap icon = iconInfo.loadIcon(c, info, context);
        // the fallback icon
        if (icon == null) {
            icon = mIconCache.getDefaultIcon(info.user);
            info.usingFallbackIcon = true;
        }
        info.setIcon(icon);
        return info;
    }

    @Thunk
    ShortcutInfo getShortcutInfo(ResponeAppsShortcut deskAppsBean, Context context) {
        final ShortcutInfo info = new ShortcutInfo();
        // Non-app shortcuts are only supported for current user.
        info.user = UserHandleCompat.myUserHandle();
        info.itemType = LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT;

        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(deskAppsBean.getPackageName());
        //假数据（actitivy是假的）
        intent.setComponent(new ComponentName(deskAppsBean.getPackageName(), ".Activity"));

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        long serialNumber = UserManagerCompat.getInstance(context).getSerialNumberForUser(info.user);
        intent.putExtra(ItemInfo.EXTRA_PROFILE, serialNumber);
        intent.putExtra(CommonConstants.PRESET_EXTRA_CHANNELID, deskAppsBean.getChanneld());
        intent.putExtra(CommonConstants.PRESET_EXTRA_AUTO_DOWNLOAD, deskAppsBean.isAutoDownload());

        info.apkObsUrl = deskAppsBean.getApkObsUrl();
        info.intent = intent;
        info.title = deskAppsBean.getAppName();
        info.apkIconUrl = deskAppsBean.getAppImgUrl();
        info.sortIndex = deskAppsBean.getSortIndex();
        return info;
    }

    ShortcutInfo infoFromShortcutIntent(Context context, Intent data) {
        Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        String name = data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        Parcelable bitmap = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);

        if (intent == null) {
            // If the intent is null, we can't construct a valid ShortcutInfo, so we return null
            Log.e(TAG, "Can't construct ShorcutInfo with null intent");
            return null;
        }

        Bitmap icon = null;
        boolean customIcon = false;
        ShortcutIconResource iconResource = null;

        if (bitmap instanceof Bitmap) {
            icon = Utilities.createIconBitmap((Bitmap) bitmap, context);
            customIcon = true;
        } else {
            Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
            if (extra instanceof ShortcutIconResource) {
                iconResource = (ShortcutIconResource) extra;
                icon = Utilities.createIconBitmap(iconResource.packageName,
                        iconResource.resourceName, context);
            }
        }

        final ShortcutInfo info = new ShortcutInfo();

        // Only support intents for current user for now. Intents sent from other
        // users wouldn't get here without intent forwarding anyway.
        info.user = UserHandleCompat.myUserHandle();
        if (icon == null) {
            icon = mIconCache.getDefaultIcon(info.user);
            info.usingFallbackIcon = true;
        }
        info.setIcon(icon);

        info.title = Utilities.trim(name);
        info.contentDescription = mUserManager.getBadgedLabelForUser(info.title, info.user);
        info.intent = intent;
        info.customIcon = customIcon;
        info.iconResource = iconResource;

        return info;
    }

    /**
     * Return an existing FolderInfo object if we have encountered this ID previously,
     * or make a new one.
     */
    @Thunk
    static FolderInfo findOrMakeFolder(LongArrayMap<FolderInfo> folders, long id) {
        // See if a placeholder was created for us already
        FolderInfo folderInfo = folders.get(id);
        if (folderInfo == null) {
            // No placeholder -- create a new instance
            folderInfo = new FolderInfo();
            folders.put(id, folderInfo);
        }
        return folderInfo;
    }


    static boolean isValidProvider(AppWidgetProviderInfo provider) {
        return (provider != null) && (provider.provider != null)
                && (provider.provider.getPackageName() != null);
    }

    public void dumpState() {
        Log.d(TAG, "mCallbacks=" + mCallbacks);
        AppInfo.dumpApplicationInfoList(TAG, "mAllAppsList.data", mBgAllAppsList.data);
        AppInfo.dumpApplicationInfoList(TAG, "mAllAppsList.added", mBgAllAppsList.added);
        AppInfo.dumpApplicationInfoList(TAG, "mAllAppsList.removed", mBgAllAppsList.removed);
        AppInfo.dumpApplicationInfoList(TAG, "mAllAppsList.modified", mBgAllAppsList.modified);
        if (mLoaderTask != null) {
            mLoaderTask.dumpState();
        } else {
            Log.d(TAG, "mLoaderTask=null");
        }
    }

    public Callbacks getCallback() {
        return mCallbacks != null ? mCallbacks.get() : null;
    }

    /**
     * @return {@link FolderInfo} if its already loaded.
     */
    public FolderInfo findFolderById(Long folderId) {
        synchronized (sBgLock) {
            return sBgFolders.get(folderId);
        }
    }

    /**
     * @return the looper for the worker thread which can be used to start background tasks.
     */
    public static Looper getWorkerLooper() {
        return sWorkerThread.getLooper();
    }


    /**
     * 查找是否存在已有的图标信息，如果有就将信息替换为新的
     *
     * @param oldItemInfo             旧的
     * @param presetAppsData          新的数据
     * @param bothOldAndNewExistItems 放新的
     */
    void fillBothOldAndNewExistItem(Context context, ShortcutInfo oldItemInfo, List<ResponeAppsShortcut> presetAppsData,
                                    ArrayList<ShortcutInfo> bothOldAndNewExistItems) {
        if (oldItemInfo instanceof ShortcutInfo) {
            ResponeAppsShortcut responeAppsShortcut;
            for (int i = 0; i < presetAppsData.size(); ++i) {
                responeAppsShortcut = presetAppsData.get(i);
                if (responeAppsShortcut.getPackageName().equals(oldItemInfo.getIntent().getPackage())) {
                    ShortcutInfo shortcutInfo = getShortcutInfo(responeAppsShortcut, context);
                    shortcutInfo.setIcon(((ShortcutInfo) oldItemInfo).getIcon(mIconCache));
                    shortcutInfo.container = oldItemInfo.container;
                    bothOldAndNewExistItems.add(shortcutInfo);
                }
            }
        }
    }

    boolean isOldExistNewNotExist(ItemInfo info, List<ResponeAppsShortcut> presetAppsData) {
        boolean ret = true;
        for (int i = 0; i < presetAppsData.size(); ++i) {
            if (presetAppsData.get(i).getPackageName().equals(info.getIntent().getPackage())) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    boolean isOnlyNewExist(ResponeAppsShortcut deskAppsBean, ArrayList<ShortcutInfo> presetItemsIdMap) {
        boolean ret = true;
        for (int i = 0; i < presetItemsIdMap.size(); ++i) {
            if (deskAppsBean.getPackageName().equals(presetItemsIdMap.get(i).getIntent().getPackage())) {
                ret = false;
                break;
            }
        }

        return ret;
    }


    @Override
    public void create(final List<ResponeAppsShortcut> presetAppsData1) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                final Context context = LauncherAppState.getInstance().getContext();

                //如果本地安装过该包了，则不要预置进来了
                List<ResponeAppsShortcut> presetAppsData = new ArrayList<>();
                for (ResponeAppsShortcut shortcut : presetAppsData1) {
                    if (isValidShortcut(shortcut)) {
                        presetAppsData.add(shortcut);
                    } else {
                        Log.i("shortcut", "add  pass(ApkObsUrl==null) " + shortcut.getPackageName());
                    }
                }

                //先删掉旧的预置图标（之前配置的已经存在数据库，可能和当前配置的不同，需要删除，否则旧的也会显示在桌面上）
                //旧的配置和新的配置都存在的项，装新的来修改
                final ArrayList<ShortcutInfo> bothOldAndNewExistItems = new ArrayList<>();
                ArrayList<ItemInfo> oldExistNewNotExit = new ArrayList<>();  //旧的有配置和新的没有配置
                ArrayList<ResponeAppsShortcut> onlyNewExist = new ArrayList<>(); //只有新配置的才有

                synchronized (sBgLock) {
                    //本地文件夹
                    List<Long> localFolderList = new ArrayList<>();
                    //本地文件夹和图标的对应关系
                    Map<Long, List<String>> folderIdMapShortcutList = new HashMap<>();
                    //处理本地数据库图标和文件夹的关系数据
                    accessPresetFolder(context, localFolderList, folderIdMapShortcutList);

                    //因为旧指令没有文件夹，所以旧指令仅兼容文件夹id为0的图标
                    //新版本中，folderId为0表示为桌面图标
                    List<String> packageNameList = folderIdMapShortcutList.get(0L);
                    ArrayList<ShortcutInfo> singleShortcutInfoList = new ArrayList<>();

                    if (packageNameList == null) {
                        packageNameList = new ArrayList<>();
                    }

                    for (ShortcutInfo shortcutInfo : sPresetItemsIdList) {
                        if (shortcutInfo.getIntent() != null && shortcutInfo.getIntent().getComponent() != null
                                && packageNameList.contains(shortcutInfo.getIntent().getComponent().getPackageName())) {
                            singleShortcutInfoList.add(shortcutInfo);
                        }
                    }

                    for (ShortcutInfo info : singleShortcutInfoList) {
                        fillBothOldAndNewExistItem(
                                context, info, presetAppsData, bothOldAndNewExistItems
                        );

                        if (isOldExistNewNotExist(info, presetAppsData)) {
                            oldExistNewNotExit.add(info);
                        }
                    }

                    for (int i = 0; i < presetAppsData.size(); ++i) {
                        if (isOnlyNewExist(presetAppsData.get(i), singleShortcutInfoList)) {
                            onlyNewExist.add(presetAppsData.get(i));
                        }
                        //保存蜂窝的快捷方式
                        if (presetAppsData.get(i).getAppType() == CommonConstants.SHORTCUT_SOURCE_FENGWO) {
                            downloadingFengWo.put(presetAppsData.get(i).getChanneld(), presetAppsData.get(i));
                        }
                    }
                }

                //加载(只有新配置和旧配置都有的)预置图标到桌面
                compatOldModifyList(mApp.getContext(), bothOldAndNewExistItems);

                //删除(旧的配置有但是新的配置么有的)预置图标
                delete(oldExistNewNotExit);

                Log.i("shortcut", "create onlyNewExist size " + onlyNewExist.size() + ", bothOldAndNewExistItems size " + bothOldAndNewExistItems.size() + ", OldExistNewNotExit size " + oldExistNewNotExit.size());
                //加载(只有新配置才有的)预置图标到桌面
                for (int i = 0; i < onlyNewExist.size(); ++i) {
                    final ResponeAppsShortcut deskAppsBean = onlyNewExist.get(i);
                    add(deskAppsBean);
                }
            }
        };
        runOnWorkerThread(r);
    }

    private ShortcutInfo getExistShortcutInfoFormSPresetItemListByPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName) || sPresetItemsIdList.isEmpty()) {
            return null;
        }
        synchronized (sBgLock) {
            for (ShortcutInfo shortcutInfo : sPresetItemsIdList) {
                if (shortcutInfo.getIntent() != null && shortcutInfo.getIntent().getComponent() != null
                        && TextUtils.equals(packageName, shortcutInfo.getIntent().getComponent().getPackageName())) {
                    return shortcutInfo;
                }
            }
            return null;
        }
    }

    /**
     * 因为2.2.86版本中，旧的方式在修改时用了旧的addAndBindAddedWorkspaceItems()。旧的addAndBindAddedWorkspaceItems()，也被用在了修改功能。
     * 在文件夹中，有大的应用安装完成，文件夹删除该快捷方式，并添加应用图标时，有很大的几率sPresetItemList中还存在快捷方式图标。
     * 从而旧的方法出现判断上的问题，导致新的图标替换了旧的图标，出现在了文件夹中
     *
     * @param context
     * @param bothOldAndNewExistItems
     */
    private void compatOldModifyList(Context context, ArrayList<ShortcutInfo> bothOldAndNewExistItems) {
        for (final ShortcutInfo shortcutInfo : bothOldAndNewExistItems) {
            if (shortcutInfo.getIntent() == null || shortcutInfo.getIntent().getComponent() == null
                    || TextUtils.isEmpty(shortcutInfo.getIntent().getComponent().getPackageName())) {
                continue;
            }

            synchronized (sBgLock) {
                ShortcutInfo existShortcutInfo = getExistShortcutInfoFormSPresetItemListByPackageName(
                        shortcutInfo.getIntent().getComponent().getPackageName()
                );

                if (existShortcutInfo != null) {
                    useNewShortcutInfoArgument(existShortcutInfo, shortcutInfo);
                    FolderInfo folderInfo = sBgFolders.get(existShortcutInfo.container);
                    if (folderInfo != null) {
                        int index = folderInfo.contents.indexOf(existShortcutInfo);
                        if (index >= 0) {
                            folderInfo.contents.set(index, shortcutInfo);
                        }
                    }
                    Log.i(TAG, "compatModifyData：" + shortcutInfo.title);
                    addOrMoveItemInDatabaseById(
                            context, shortcutInfo, shortcutInfo.container,
                            shortcutInfo.screenId, shortcutInfo.cellX, shortcutInfo.cellY
                    );
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            getCallback().bindPresetShortcutsChanged(shortcutInfo);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void add(final ResponeAppsShortcut app) {
        //保存蜂窝的快捷方式
        if (app.getAppType() == CommonConstants.SHORTCUT_SOURCE_FENGWO) {
            downloadingFengWo.put(app.getChanneld(), app);
        }
        //需求：桌面屏蔽掉有配置开启但是没有OBS地址的快捷方式
        if (app.getApkObsUrl() == null
                || app.getApkObsUrl().isEmpty()) {
            Log.i("shortcut", "add  pass(ApkObsUrl==null) " + app.getPackageName());
            return;
        }

        Runnable r = new Runnable() {
            public void run() {

                RequestBuilder<Drawable> errReq = Glide.with(mApp.getContext()).load(R.drawable.ic_all_apps_bg_icon_1);

                Glide.with(mApp.getContext()).load(app.getAppImgUrl())
//                        .error(errReq)
                        .into(new SimpleTarget<Drawable>() {
                                  @Override
                                  public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                                      final ShortcutInfo shortcutInfo = getShortcutInfo(app, mApp.getContext());
                                      shortcutInfo.setIcon(((BitmapDrawable) resource).getBitmap());
                                      ArrayList<ItemInfo> webPresetApps = new ArrayList<ItemInfo>();
                                      webPresetApps.add(shortcutInfo);
                                      addAndBindAddedWorkspaceItems(mApp.getContext(), webPresetApps, true);

                                      ContentValues contentValues = new ContentValues();
                                      contentValues.put(PresetFolderProvider.PresetFolder.PACKAGE_NAME, app.getPackageName());
                                      mApp.getContext().getContentResolver()
                                              .insert(PresetFolderProvider.PresetFolder.getInsertUri(0), contentValues);

                                      sPresetItemsIdList.add(shortcutInfo);
                                      Log.i("shortcut", "addAndBindAddedWorkspaceItems " + app.getPackageName() + " ,sPresetItemsIdList size " + sPresetItemsIdList.size());
                                  }
                              }
                        );
            }
        };
        runOnMainThread(r);
    }

    @Override
    public void delete(ResponeAppsShortcut app) {
        Log.e(TAG, "delete(ResponeAppsShortcut app)");
        final UserHandleCompat user = UserHandleCompat.myUserHandle();
        for (ItemInfo info : sPresetItemsIdList) {
            if (info.getIntent().getPackage().equals(app.getPackageName())) {
                onPackageRemoved(app.getPackageName(), user);
            }
        }
    }

    public void delete(final List<ItemInfo> list) {
        Log.e(TAG, "delete(ResponeAppsShortcut app)");
        final UserHandleCompat user = UserHandleCompat.myUserHandle();
        for (ItemInfo info : list) {
            onPackageRemoved(info.getIntent().getPackage(), user);
        }
    }

    public static boolean isExistShortcut(String name) {
        synchronized (sBgLock) {
            for (int i = 0; i < sBgWorkspaceItems.size(); ++i) {
                if (name.equals(LauncherModel.sBgWorkspaceItems.get(i).title)
                        && sBgWorkspaceItems.get(i).itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isExistPreset(long channelID) {
        synchronized (sBgLock) {
            for (int i = 0; i < sBgWorkspaceItems.size(); ++i) {
                if (channelID == LauncherModel.sBgWorkspaceItems.get(i).getIntent().getLongExtra(CommonConstants.PRESET_EXTRA_CHANNELID, 0)
                        && sBgWorkspaceItems.get(i).itemType == LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void progress(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void downloadCompleted(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void installCompleted(DownloadFileInfo downloadFileInfo) {
        ToastUtils.showShort(String.format(Utils.getApp().getString(R.string.install_success), downloadFileInfo.getTag(CommonConstants.PRESET_EXTRA_APPNAME)));
        //蜂窝安装后生成专区快捷方式
        ResponeAppsShortcut shortcut = downloadingFengWo.get(downloadFileInfo.getDownloadID());
        if (shortcut != null && shortcut.getAppType() == CommonConstants.SHORTCUT_SOURCE_FENGWO) {
            AppUtil.setFengwoTopicShotcut(shortcut.getId(), shortcut.getFengwoName(), shortcut.getAppImgUrl(), null);
        }
        if (downloadFileInfo instanceof ShortDownloadFileInfo) {//如果是预置应用下载完成
            ShortcutInfo shortcutInfo = ((ShortDownloadFileInfo) downloadFileInfo).getShortcutInfo();
            FolderInfo folderInfo = sBgFolders.get(shortcutInfo.container);
            deleteItemFromDatabaseDirect(LauncherAppState.getInstance().getContext(), shortcutInfo);
            if (folderInfo != null) {
                if (folderInfo.contents.contains(shortcutInfo)) {
                    folderInfo.remove(shortcutInfo);
                }
                Launcher launcher = (Launcher) getCallback();
                launcher.closeFolder();
            } else {
                deleteDesktopShortcut(shortcutInfo);
            }
        }
    }

    @Override
    public void error(DownloadFileInfo downloadFileInfo, int errorCode, String errorMessage) {
        ResponeAppsShortcut shortcut = downloadingFengWo.get(downloadFileInfo.getDownloadID());
        if (shortcut != null && shortcut.getAppType() == CommonConstants.SHORTCUT_SOURCE_FENGWO) {
            downloadingFengWo.remove(shortcut.getChanneld());
        }
    }

    @Override
    public void startDownload(final ResponeAppsShortcut app) {
        synchronized (downloadItemViews) {
            if (downloadItemViews.containsKey(app.getChanneld())) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadItemViews.get(app.getChanneld()).performClick();
                    }
                });
            }
        }
    }

    @Override
    public void saveDownloadInfo(final Long Channeld, View view) {
        if (Channeld == 0) {
            LogUtils.eTag("shortcut", "saveDownloadInfo Channeld==0");
            return;
        }

        synchronized (downloadItemViews) {
            downloadItemViews.put(Channeld, view);
        }
    }

    public void arrangeDesktop(final String sortTable) {
        //计算耗时均值
        if (mockTimer.size() == 100) {
            long timeCount = 0;
            for (long time : mockTimer) {
                timeCount += time;
            }
            Log.e("arrangeDesktop", "cost time time average " + (timeCount / 100));
            return;
        }

        Log.e("arrangeDesktop", "commit arrange in background ");
        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                Log.e("arrangeDesktop", Thread.currentThread().getName() + "start to arrange：" + SystemClock.uptimeMillis());
                Context context = LauncherAppState.getInstance().getContext();

                final long t = SystemClock.uptimeMillis();

                synchronized (sBgLock) {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ArrangeBean>() {
                        }.getType();

                        ArrangeBean arrangeBean = gson.fromJson(sortTable, type);
                        //排桌面
                        List<String> arrangeItemList = arrangeBean.desktopData;
                        //排热座
                        List<String> arrangeHeatSeatItemList = arrangeBean.heatSeatData;
                        //从数据库获取所有的图标数据
                        List<ItemInfo> allItemInfoList = new ArrayList<>();
                        //热座信息
                        boolean[] heatSeatCondition = new boolean[4];
                        accessAllItemInfoFromDb(context, arrangeItemList, allItemInfoList, heatSeatCondition);

                        Log.e("获取的所有item", allItemInfoList.toString());

                        String arrangePackageName;
                        List<ItemInfo> workSpaceApps = new ArrayList<>();
                        List<Long> removeIdList = new ArrayList<>();
                        ItemInfo oldItemInfo;
                        List<ItemInfo> packageSortList;

                        if (arrangeItemList != null && !arrangeItemList.isEmpty()) {
                            //按排序表排序桌面
                            for (int i = 0; i < arrangeItemList.size(); i++) {
                                arrangePackageName = arrangeItemList.get(i);
                                packageSortList = new ArrayList<>();

                                for (int j = 0; j < allItemInfoList.size(); j++) {
                                    oldItemInfo = allItemInfoList.get(j);
                                    if (oldItemInfo instanceof LauncherAppWidgetInfo) {
                                        continue;
                                    }

//                                    if (oldItemInfo.getIntent().getComponent() == null) {
//                                        continue;
//                                    }

                                    if (TextUtils.equals(oldItemInfo.getIntent().getComponent().getPackageName(), arrangePackageName)) {
                                        packageSortList.add(oldItemInfo);
                                        removeIdList.add(oldItemInfo.id);
                                    }
                                }
                                //如果文件夹相同，按包名排序
                                Collections.sort(packageSortList, new Comparator<ItemInfo>() {
                                    @Override
                                    public int compare(ItemInfo o1, ItemInfo o2) {
                                        if (!(o1 instanceof ShortcutInfo)
                                                || !(o2 instanceof ShortcutInfo)) {
                                            return 0;
                                        }

                                        return ((ShortcutInfo) o1).getTargetComponent().getPackageName()
                                                .compareTo(((ShortcutInfo) o2).getTargetComponent().getPackageName());
                                    }
                                });

                                workSpaceApps.addAll(packageSortList);
                            }
                        }

                        List<ItemInfo> realArrangeHeatSeatItemList = new ArrayList<>();
                        if (arrangeHeatSeatItemList != null && !arrangeHeatSeatItemList.isEmpty()) {
                            for (int i = 0; i < arrangeHeatSeatItemList.size(); i++) {
                                arrangePackageName = arrangeHeatSeatItemList.get(i);
                                for (int j = 0; j < allItemInfoList.size(); j++) {
                                    oldItemInfo = allItemInfoList.get(j);
                                    if (!(oldItemInfo instanceof ShortcutInfo)) {
                                        continue;
                                    }

                                    if (oldItemInfo.getIntent().getComponent() == null) {
                                        continue;
                                    }

                                    if (TextUtils.equals(oldItemInfo.getIntent().getComponent().getPackageName(), arrangePackageName)) {
                                        oldItemInfo.sortIndex = i;
                                        realArrangeHeatSeatItemList.add(oldItemInfo);
                                        removeIdList.add(oldItemInfo.id);
                                    }
                                    //对热座信息进行排序
                                    Collections.sort(realArrangeHeatSeatItemList, new Comparator<ItemInfo>() {
                                        @Override
                                        public int compare(ItemInfo o1, ItemInfo o2) {
                                            return o1.sortIndex - o2.sortIndex;
                                        }
                                    });
                                }
                            }
                        }

                        //移除排序后的元素
                        Iterator<ItemInfo> iterator = allItemInfoList.iterator();
                        ItemInfo next;
                        while (iterator.hasNext()) {
                            next = iterator.next();
                            if (removeIdList.contains(next.id)) {
                                iterator.remove();
                            }
                            if (next.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
                                    || next.itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_APPWIDGET) {
                                workSpaceApps.add(next);
                            }
                        }

                        allItemInfoList = ArrangeUtils.arrange(allItemInfoList);

                        //补足剩余元素
                        workSpaceApps.addAll(allItemInfoList);
                        //补足热座
                        workSpaceApps.addAll(realArrangeHeatSeatItemList);

                        if (!workSpaceApps.isEmpty()) {
                            //将文件夹放到最前面

                            //整理数据库，然后重启的流程
                            final ArrayList<Long> addedWorkspaceScreensFinal = new ArrayList<Long>();
                            ArrayList<Long> workspaceScreens = loadWorkspaceScreensDb(context);

                            for (ItemInfo item : workSpaceApps) {

                                Pair<Long, int[]> coords;
                                long screenId;
                                int[] cordinates;
                                long container = LauncherSettings.Favorites.CONTAINER_DESKTOP;
                                if (item.itemType != LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET
                                        && item.itemType != LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_APPWIDGET) {
                                    if (item.container != LauncherSettings.Favorites.CONTAINER_HOTSEAT
                                            && item.itemType != LauncherSettings.Favorites.ITEM_TYPE_FOLDER
                                            && isItemInHeatSeatList(item, arrangeHeatSeatItemList)) {
                                        int spaceForItemInHeatSeat = findSpaceForItemInHeatSeat(heatSeatCondition);
                                        if (spaceForItemInHeatSeat != -1) {//热座有位置
                                            container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
                                            screenId = spaceForItemInHeatSeat;
                                            cordinates = new int[]{spaceForItemInHeatSeat, 0};
                                            heatSeatCondition[spaceForItemInHeatSeat] = true;
                                        } else {//热座无位置
                                            coords = findSpaceForItem(context,
                                                    workspaceScreens, addedWorkspaceScreensFinal,
                                                    1, 1);
                                            screenId = coords.first;
                                            cordinates = coords.second;
                                        }
                                    } else {
                                        if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                                            heatSeatCondition[(int) item.screenId] = false;
                                        }
                                        coords = findSpaceForItem(context,
                                                workspaceScreens, addedWorkspaceScreensFinal,
                                                1, 1);
                                        screenId = coords.first;
                                        cordinates = coords.second;
                                    }
                                } else {
                                    //控件位置不动
                                    //该行代码用于控件填充屏幕位置
                                    findSpaceForItem(context,
                                            workspaceScreens, addedWorkspaceScreensFinal,
                                            item.spanX, item.spanY);
                                    screenId = item.screenId;
                                    cordinates = new int[]{item.cellX, item.cellY};
                                }

                                Log.e(TAG, item.title + ",screenId:" + screenId + ",container:" + container + ",xy:" + Arrays.toString(cordinates));
                                modifyItemInDatabaseAndAddWorkspace(
                                        context, item,
                                        container,
                                        screenId,
                                        cordinates[0],
                                        cordinates[1],
                                        item.spanX,
                                        item.spanY
                                );
                            }
                            Log.e("arrangeDesktop", "db cost time:" + (SystemClock.uptimeMillis() - t));
                            mockTimer.add((SystemClock.uptimeMillis() - t));

                            updateWorkspaceScreenOrder(context, workspaceScreens);

                            resetLoadedState(true, true);
                            startLoader(PagedView.INVALID_RESTORE_PAGE, LauncherModel.LOADER_FLAG_NONE);
                        }
                    } catch (Exception e) {
                        Log.e("arrangeDesktop", "parse error");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //是否在热座数据里
    private boolean isItemInHeatSeatList(ItemInfo item, List<String> arrangeHeatSeatItemList) {
        if (arrangeHeatSeatItemList == null || arrangeHeatSeatItemList.isEmpty()) {
            return false;
        }
        for (String packageName : arrangeHeatSeatItemList) {
            if (item.getIntent() != null
                    && item.getIntent().getComponent() != null
                    && TextUtils.equals(item.getIntent().getComponent().getPackageName(), packageName)) {
                return true;
            }
        }
        return false;
    }

    //获取热座上的空位置
    private int findSpaceForItemInHeatSeat(boolean[] heatSeatCondition) {
        for (int i = 0; i < heatSeatCondition.length; i++) {
            if (!heatSeatCondition[i]) {
                return i;
            }
        }
        return -1;
    }

    //整理桌面用的获取数据库图标
    private void accessAllItemInfoFromDb(Context context, List<String> arrangeItemList, List<ItemInfo> allItemInfoList, boolean[] heatSeatCondition) {
        final ContentResolver cr = context.getContentResolver();

        final PackageManager manager = context.getPackageManager();
        final boolean isSafeMode = manager.isSafeMode();
        final LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(context);
        final boolean isSdCardReady = context.registerReceiver(null,
                new IntentFilter(StartupReceiver.SYSTEM_READY)) != null;

        synchronized (sBgLock) {
            //已经出现的图标的位置
            final LongArrayMap<ItemInfo[][]> occupied = new LongArrayMap<>();
            clearSBgDataStructures();
            final HashMap<String, Integer> installingPkgs = PackageInstallerCompat
                    .getInstance(context).updateAndGetActiveSessionCache();
            sBgWorkspaceScreens.addAll(loadWorkspaceScreensDb(context));
            final Uri contentUri = LauncherSettings.Favorites.CONTENT_URI;


            Cursor c = cr.query(contentUri, null, null, null, null);

            try {
                final int idIndex = c.getColumnIndexOrThrow(LauncherSettings.Favorites._ID);
                final int intentIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.INTENT);
                final int titleIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.TITLE);
                final int containerIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.CONTAINER);
                final int itemTypeIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.ITEM_TYPE);
                final int appWidgetIdIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.APPWIDGET_ID);
                final int appWidgetProviderIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.APPWIDGET_PROVIDER);
                final int screenIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.SCREEN);
                final int cellXIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.CELLX);
                final int cellYIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.CELLY);
                final int spanXIndex = c.getColumnIndexOrThrow
                        (LauncherSettings.Favorites.SPANX);
                final int spanYIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.SPANY);
                final int rankIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.RANK);
                final int restoredIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.RESTORED);
                final int profileIdIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.PROFILE_ID);
                final int optionsIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.OPTIONS);
                final int downloadUrlIndex = c.getColumnIndexOrThrow(
                        LauncherSettings.Favorites.DOWNLOAD_URL);
                final CursorIconInfo cursorIconInfo = new CursorIconInfo(c);

                final LongSparseArray<UserHandleCompat> allUsers = new LongSparseArray<>();
                for (UserHandleCompat user : mUserManager.getUserProfiles()) {
                    allUsers.put(mUserManager.getSerialNumberForUser(user), user);
                }

                ShortcutInfo info;
                String intentDescription;
                LauncherAppWidgetInfo appWidgetInfo;
                int container;
                long id;
                long serialNumber;
                Intent intent;
                UserHandleCompat user;

                while (c.moveToNext()) {
                    try {
                        int itemType = c.getInt(itemTypeIndex);
                        boolean restored = 0 != c.getInt(restoredIndex);
                        boolean allowMissingTarget = false;
                        container = c.getInt(containerIndex);

                        switch (itemType) {
                            case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                            case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                            case LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT:
                                id = c.getLong(idIndex);
                                intentDescription = c.getString(intentIndex);
                                serialNumber = c.getInt(profileIdIndex);
                                user = allUsers.get(serialNumber);
                                int promiseType = c.getInt(restoredIndex);
                                int disabledState = 0;
                                boolean itemReplaced = false;
                                if (user == null) {
                                    continue;
                                }

                                try {
                                    intent = Intent.parseUri(intentDescription, 0);
                                    ComponentName cn = intent.getComponent();
                                    if (cn != null && cn.getPackageName() != null) {
                                        boolean validPkg = launcherApps.isPackageEnabledForProfile(
                                                cn.getPackageName(), user);
                                        boolean validComponent = validPkg &&
                                                launcherApps.isActivityEnabledForProfile(cn, user);

                                        if (validComponent) {
                                            if (restored) {
                                                restored = false;
                                            }
                                        } else if (validPkg) {
                                            intent = null;
                                            if ((promiseType & ShortcutInfo.FLAG_AUTOINTALL_ICON) != 0) {
                                                intent = manager.getLaunchIntentForPackage(
                                                        cn.getPackageName());
                                                if (intent != null) {
                                                    ContentValues values = new ContentValues();
                                                    values.put(LauncherSettings.Favorites.INTENT,
                                                            intent.toUri(0));
                                                    updateItem(id, values);
                                                }
                                            }

                                            if (intent == null) {
                                                continue;
                                            } else {
                                                restored = false;
                                            }
                                        } else if (restored) {
                                            if ((promiseType & ShortcutInfo.FLAG_RESTORE_STARTED) != 0) {
                                                // Restore has started once.
                                            } else if (installingPkgs.containsKey(cn.getPackageName())) {
                                                promiseType |= ShortcutInfo.FLAG_RESTORE_STARTED;
                                                ContentValues values = new ContentValues();
                                                values.put(LauncherSettings.Favorites.RESTORED,
                                                        promiseType);
                                                updateItem(id, values);
                                            } else if ((promiseType & ShortcutInfo.FLAG_RESTORED_APP_TYPE) != 0) {
                                                int appType = CommonAppTypeParser.decodeItemTypeFromFlag(promiseType);
                                                CommonAppTypeParser parser = new CommonAppTypeParser(id, appType, context);
                                                if (parser.findDefaultApp()) {
                                                    intent = parser.parsedIntent;
                                                    cn = intent.getComponent();
                                                    ContentValues values = parser.parsedValues;
                                                    values.put(LauncherSettings.Favorites.RESTORED, 0);
                                                    updateItem(id, values);
                                                    restored = false;
                                                    itemReplaced = true;
                                                } else if (REMOVE_UNRESTORED_ICONS) {
                                                    continue;
                                                }
                                            } else if (REMOVE_UNRESTORED_ICONS) {
                                                continue;
                                            }
                                        } else if (launcherApps.isAppEnabled(
                                                manager, cn.getPackageName(),
                                                PackageManager.GET_UNINSTALLED_PACKAGES)) {
                                            allowMissingTarget = true;
                                            disabledState = ShortcutInfo.FLAG_DISABLED_NOT_AVAILABLE;
                                        } else if (!isSdCardReady) {
                                            HashSet<String> pkgs = sPendingPackages.get(user);
                                            if (pkgs == null) {
                                                pkgs = new HashSet<String>();
                                                sPendingPackages.put(user, pkgs);
                                            }
                                            pkgs.add(cn.getPackageName());
                                            allowMissingTarget = true;

                                        } else if (itemType == LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                                        } else {
                                            continue;
                                        }
                                    } else if (cn == null) {
                                        restored = false;
                                    }
                                } catch (URISyntaxException e) {
                                    Launcher.addDumpLog(TAG,
                                            "Invalid uri: " + intentDescription, true);
                                    continue;
                                }

                                boolean useLowResIcon = container >= 0 &&
                                        c.getInt(rankIndex) >= FolderIcon.NUM_ITEMS_IN_PREVIEW;

                                if (itemReplaced) {
                                    if (user.equals(UserHandleCompat.myUserHandle())) {
                                        info = getAppShortcutInfo(manager, intent, user, context, null,
                                                cursorIconInfo.iconIndex, titleIndex,
                                                false, useLowResIcon);
                                    } else {
                                        continue;
                                    }
                                } else if (restored) {
                                    if (user.equals(UserHandleCompat.myUserHandle())) {
                                        Launcher.addDumpLog(TAG,
                                                "constructing info for partially restored package",
                                                true);
                                        info = getRestoredItemInfo(c, titleIndex, intent,
                                                promiseType, itemType, cursorIconInfo, context);
                                        intent = getRestoredItemIntent(c, context, intent);
                                    } else {
                                        continue;
                                    }
                                } else if (itemType ==
                                        LauncherSettings.Favorites.ITEM_TYPE_APPLICATION) {
                                    info = getAppShortcutInfo(manager, intent, user, context, c,
                                            cursorIconInfo.iconIndex, titleIndex,
                                            allowMissingTarget, useLowResIcon);
                                } else if (itemType ==
                                        LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                                    info = getPresetShortcutInfo(c, context, titleIndex, cursorIconInfo);
                                    if (intent.getAction() != null &&
                                            intent.getCategories() != null &&
                                            intent.getAction().equals(Intent.ACTION_MAIN) &&
                                            intent.getCategories().contains(Intent.CATEGORY_LAUNCHER)) {
                                        intent.addFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                    }
                                } else {
                                    info = getShortcutInfo(c, context, titleIndex, cursorIconInfo);

                                    // App shortcuts that used to be automatically added to Launcher
                                    // didn't always have the correct intent flags set, so do that
                                    // here
                                    if (intent.getAction() != null &&
                                            intent.getCategories() != null &&
                                            intent.getAction().equals(Intent.ACTION_MAIN) &&
                                            intent.getCategories().contains(Intent.CATEGORY_LAUNCHER)) {
                                        intent.addFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                    }
                                }

                                if (info != null) {
                                    info.id = id;
                                    info.intent = intent;
                                    info.container = container;
                                    info.screenId = c.getInt(screenIndex);
                                    info.cellX = c.getInt(cellXIndex);
                                    info.cellY = c.getInt(cellYIndex);
                                    info.rank = c.getInt(rankIndex);
                                    info.spanX = 1;
                                    info.spanY = 1;
                                    info.apkObsUrl = c.getString(downloadUrlIndex);
                                    info.intent.putExtra(ItemInfo.EXTRA_PROFILE, serialNumber);


                                    if (info.promisedIntent != null) {
                                        info.promisedIntent.putExtra(ItemInfo.EXTRA_PROFILE, serialNumber);
                                    }
                                    info.isDisabled = disabledState;
                                    if (isSafeMode && !Utilities.isSystemApp(context, intent)) {
                                        info.isDisabled |= ShortcutInfo.FLAG_DISABLED_SAFEMODE;
                                    }

                                    if (!checkItemPlacement(occupied, info, sBgWorkspaceScreens)) {
                                        break;
                                    }

                                    if (restored) {
                                        ComponentName cn = info.getTargetComponent();
                                        if (cn != null) {
                                            Integer progress = installingPkgs.get(cn.getPackageName());
                                            if (progress != null) {
                                                info.setInstallProgress(progress);
                                            } else {
                                                info.status &= ~ShortcutInfo.FLAG_INSTALL_SESSION_ACTIVE;
                                            }
                                        }
                                    }

                                    switch (container) {
                                        case LauncherSettings.Favorites.CONTAINER_DESKTOP:
                                            allItemInfoList.add(info);
                                            break;
                                        case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
                                            heatSeatCondition[(int) info.screenId] = true;
                                            if (info.getIntent() != null && info.getIntent().getComponent() != null
                                                    && !TextUtils.isEmpty(info.getIntent().getComponent().getPackageName())
                                                    && arrangeItemList.contains(info.getIntent().getComponent().getPackageName())) {
                                                allItemInfoList.add(info);
                                            }
                                            break;
                                        default:
                                            // Item is in a user folder
                                            FolderInfo folderInfo =
                                                    findOrMakeFolder(sBgFolders, container);
                                            folderInfo.add(info);
                                            if (folderInfo.id != -1 && folderInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                                                allItemInfoList.add(folderInfo);
                                            }
                                            break;
                                    }
                                } else {
                                    throw new RuntimeException("Unexpected null ShortcutInfo");
                                }
                                break;

                            case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                                id = c.getLong(idIndex);
                                FolderInfo folderInfo = findOrMakeFolder(sBgFolders, id);

                                // Do not trim the folder label, as is was set by the user.
                                folderInfo.title = c.getString(titleIndex);
                                folderInfo.id = id;
                                folderInfo.container = container;
                                folderInfo.screenId = c.getInt(screenIndex);
                                folderInfo.cellX = c.getInt(cellXIndex);
                                folderInfo.cellY = c.getInt(cellYIndex);
                                folderInfo.spanX = 1;
                                folderInfo.spanY = 1;
                                folderInfo.options = c.getInt(optionsIndex);

                                // check & update map of what's occupied
                                if (!checkItemPlacement(occupied, folderInfo, sBgWorkspaceScreens)) {
                                    break;
                                }

                                switch (container) {
                                    case LauncherSettings.Favorites.CONTAINER_DESKTOP:
                                        allItemInfoList.add(folderInfo);
                                        break;
                                    case LauncherSettings.Favorites.CONTAINER_HOTSEAT://热座不整理
                                        heatSeatCondition[(int) folderInfo.screenId] = true;
                                        break;
                                }
                                break;

                            case LauncherSettings.Favorites.ITEM_TYPE_APPWIDGET:
                            case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_APPWIDGET:
                                // Read all Launcher-specific widget details
                                boolean customWidget = itemType ==
                                        LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_APPWIDGET;

                                int appWidgetId = c.getInt(appWidgetIdIndex);
                                serialNumber = c.getLong(profileIdIndex);
                                String savedProvider = c.getString(appWidgetProviderIndex);
                                id = c.getLong(idIndex);
                                user = allUsers.get(serialNumber);
                                if (user == null) {
                                    continue;
                                }

                                final ComponentName component =
                                        ComponentName.unflattenFromString(savedProvider);

                                final int restoreStatus = c.getInt(restoredIndex);
                                final boolean isIdValid = (restoreStatus &
                                        LauncherAppWidgetInfo.FLAG_ID_NOT_VALID) == 0;
                                final boolean wasProviderReady = (restoreStatus &
                                        LauncherAppWidgetInfo.FLAG_PROVIDER_NOT_READY) == 0;

                                final LauncherAppWidgetProviderInfo provider =
                                        LauncherModel.getProviderInfo(context,
                                                ComponentName.unflattenFromString(savedProvider),
                                                user);

                                final boolean isProviderReady = isValidProvider(provider);
                                if (!isSafeMode && !customWidget &&
                                        wasProviderReady && !isProviderReady) {
                                    String log = "Deleting widget that isn't installed anymore: "
                                            + "id=" + id + " appWidgetId=" + appWidgetId;

                                    Log.e(TAG, log);
                                    Launcher.addDumpLog(TAG, log, false);
                                } else {
                                    if (isProviderReady) {
                                        appWidgetInfo = new LauncherAppWidgetInfo(appWidgetId,
                                                provider.provider);

                                        // The provider is available. So the widget is either
                                        // available or not available. We do not need to track
                                        // any future restore updates.
                                        int status = restoreStatus &
                                                ~LauncherAppWidgetInfo.FLAG_RESTORE_STARTED;
                                        if (!wasProviderReady) {
                                            // If provider was not previously ready, update the
                                            // status and UI flag.

                                            // Id would be valid only if the widget restore broadcast was received.
                                            if (isIdValid) {
                                                status = LauncherAppWidgetInfo.FLAG_UI_NOT_READY;
                                            } else {
                                                status &= ~LauncherAppWidgetInfo
                                                        .FLAG_PROVIDER_NOT_READY;
                                            }
                                        }
                                        appWidgetInfo.restoreStatus = status;
                                    } else {
                                        Log.v(TAG, "Widget restore pending id=" + id
                                                + " appWidgetId=" + appWidgetId
                                                + " status =" + restoreStatus);
                                        appWidgetInfo = new LauncherAppWidgetInfo(appWidgetId,
                                                component);
                                        appWidgetInfo.restoreStatus = restoreStatus;
                                        Integer installProgress = installingPkgs.get(component.getPackageName());

                                        if ((restoreStatus & LauncherAppWidgetInfo.FLAG_RESTORE_STARTED) != 0) {
                                            // Restore has started once.
                                        } else if (installProgress != null) {
                                            // App restore has started. Update the flag
                                            appWidgetInfo.restoreStatus |=
                                                    LauncherAppWidgetInfo.FLAG_RESTORE_STARTED;
                                        } else if (REMOVE_UNRESTORED_ICONS && !isSafeMode) {
                                            Launcher.addDumpLog(TAG,
                                                    "Unrestored widget removed: " + component, true);
                                            continue;
                                        }

                                        appWidgetInfo.installProgress =
                                                installProgress == null ? 0 : installProgress;
                                    }

                                    appWidgetInfo.id = id;
                                    appWidgetInfo.screenId = c.getInt(screenIndex);
                                    appWidgetInfo.cellX = c.getInt(cellXIndex);
                                    appWidgetInfo.cellY = c.getInt(cellYIndex);
                                    appWidgetInfo.spanX = c.getInt(spanXIndex);
                                    appWidgetInfo.spanY = c.getInt(spanYIndex);
                                    appWidgetInfo.user = user;

                                    if (container != LauncherSettings.Favorites.CONTAINER_DESKTOP &&
                                            container != LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
                                        Log.e(TAG, "Widget found where container != " +
                                                "CONTAINER_DESKTOP nor CONTAINER_HOTSEAT - ignoring!");
                                        continue;
                                    }

                                    appWidgetInfo.container = container;
                                    // check & update map of what's occupied
                                    if (!checkItemPlacement(occupied, appWidgetInfo, sBgWorkspaceScreens)) {
                                        break;
                                    }

                                    if (!customWidget) {
                                        String providerName =
                                                appWidgetInfo.providerName.flattenToString();
                                        if (!providerName.equals(savedProvider) ||
                                                (appWidgetInfo.restoreStatus != restoreStatus)) {
                                            ContentValues values = new ContentValues();
                                            values.put(
                                                    LauncherSettings.Favorites.APPWIDGET_PROVIDER,
                                                    providerName);
                                            values.put(LauncherSettings.Favorites.RESTORED,
                                                    appWidgetInfo.restoreStatus);
                                            updateItem(id, values);
                                        }
                                    }
                                    if (appWidgetInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                                        allItemInfoList.add(appWidgetInfo);
                                    }
                                }
                                break;
                        }
                    } catch (Exception e) {
                        Launcher.addDumpLog(TAG, "Desktop items loading interrupted", e, true);
                    }
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }

        }
    }

    private void deleteDesktopShortcut(ShortcutInfo existItemInfo) {
        getCallback().deleteDesktopShortcut(existItemInfo);
    }

    private void addShortcutInfo(String folderName, ShortcutInfo shortcutInfo) {
        getCallback().completeAddShortcut(folderName, shortcutInfo);
    }

    private boolean isValidShortcut(ResponeAppsShortcut responeAppsShortcut) {
        return !TextUtils.isEmpty(responeAppsShortcut.getApkObsUrl()) && !AppUtils.isAppInstalled(responeAppsShortcut.getPackageName());
    }

    public static void putPackageMap(ShortcutInfo itemInfo) {
        synchronized (sBgLock) {
            if (itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT && !sPresetItemsIdList.contains(itemInfo)) {
                sPresetItemsIdList.add(itemInfo);
            }
            sBgItemsIdMap.put(itemInfo.id, itemInfo);
            if (!sBgWorkspaceItems.contains(itemInfo)) {
                sBgWorkspaceItems.add(itemInfo);
            }
        }
    }

    public static void modifyPackageMap(ShortcutInfo shortcutInfo) {
        synchronized (sBgLock) {
            sBgItemsIdMap.put(shortcutInfo.id, shortcutInfo);
            if (shortcutInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_PRESET_SHORTCUT) {
                int oldIndex = -1;
                for (int i = 0; i < sPresetItemsIdList.size(); i++) {
                    if (TextUtils.equals(sPresetItemsIdList.get(i).getIntent().getPackage(), shortcutInfo.getIntent().getPackage())) {
                        oldIndex = i;
                        break;
                    }
                }
                if (oldIndex != -1) {
                    sPresetItemsIdList.set(oldIndex, shortcutInfo);
                }
            }
            ItemInfo itemInfo;
            int oldIndex = -1;
            for (int i = 0; i < sBgWorkspaceItems.size(); i++) {
                itemInfo = sBgWorkspaceItems.get(i);
                if (itemInfo instanceof ShortcutInfo
                        && TextUtils.equals(itemInfo.getIntent().getPackage(), shortcutInfo.getIntent().getPackage())) {
                    oldIndex = i;
                    break;
                }
            }
            if (!sBgWorkspaceItems.contains(shortcutInfo) && oldIndex != -1) {
                sBgWorkspaceItems.set(oldIndex, shortcutInfo);
            }
        }
    }

    public static void putFolder(long id, FolderInfo folderInfo) {
        synchronized (sBgLock) {
            sBgFolders.put(id, folderInfo);
            sBgItemsIdMap.put(id, folderInfo);
            if (!sBgWorkspaceItems.contains(folderInfo)) {
                sBgWorkspaceItems.add(folderInfo);
            }
        }
    }

    public static void removeFolder(FolderInfo folderInfo) {
        synchronized (sBgLock) {
            sBgFolders.remove(folderInfo.id);
            sFolderLocalMap.remove(folderInfo.title.toString());
        }
    }

    @Override
    public void loadPresetShortcutData(final List<PresetShortData> presetShortDataList) {
        if (presetShortDataList == null) {
            return;
        }

        final Runnable modifyOrAddRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (sBgLock) {
                    //读取一次现在桌面的文件夹位置
                    sFolderLocalMap = new HashMap<>();
                    for (FolderInfo folderInfo : sBgFolders) {
                        if (!TextUtils.isEmpty(folderInfo.title)) {
                            sFolderLocalMap.put(
                                    folderInfo.title.toString(),
                                    Pair.create(folderInfo.screenId, new int[]{folderInfo.cellX, folderInfo.cellY})
                            );
                        }
                    }

                    //获取操作符
                    accessModifyOrAddActionList(presetShortDataList);
                }
            }
        };

        final Runnable deleteNotEqualFolderRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (sBgLock) {
                    //检查文件夹是否一致
                    List<PresetShortcutAction> deleteFolderActionList = accessDeleteNotEqualsFolderActionList(presetShortDataList);
                    deleteNotEqualsFolderActionList(deleteFolderActionList);

                    runOnMainThread(modifyOrAddRunnable);
                }
            }
        };

        final Runnable deleteFolderRunnable = new Runnable() {
            @Override
            public void run() {
                synchronized (sBgLock) {
                    //检查文件夹是否需要删除
                    List<PresetShortcutAction> deleteFolderActionList = accessDeleteFolderActionList(presetShortDataList);

                    //执行文件删除
                    executeDeleteFolderActionList(deleteFolderActionList);

                    runOnMainThread(deleteNotEqualFolderRunnable);
                }
            }
        };

        mHandler.postIdle(new Runnable() {
            @Override
            public void run() {
                //加载前先关闭文件夹，打开的文件夹会影响图标加入文件夹的逻辑
                Launcher launcher = (Launcher) getCallback();
                launcher.closeFolder();
                runOnMainThread(deleteFolderRunnable);
            }
        });
    }

    private void executeAddNewActionList(List<PresetShortcutAction> addNewActionList) {
        if (!addNewActionList.isEmpty()) {
            AtomicInteger loadPictureCounter = new AtomicInteger(0);

            List<ShortcutInfo> shortcutInfoList = new ArrayList<>();
            for (PresetShortcutAction presetShortcutAction : addNewActionList) {
                executeAddNew(presetShortcutAction, shortcutInfoList, loadPictureCounter, addNewActionList.size());
            }
        }
    }

    private void executeModifyActionList(List<PresetShortcutAction> modifyActionList) {
        if (!modifyActionList.isEmpty()) {
            for (PresetShortcutAction presetShortcutAction : modifyActionList) {
                executeModifyData(presetShortcutAction);
            }
        }
    }

    private void deleteNotEqualsFolderActionList(List<PresetShortcutAction> deleteFolderActionList) {
        if (!deleteFolderActionList.isEmpty()) {
            for (PresetShortcutAction presetShortcutAction : deleteFolderActionList) {
                executeDelete(presetShortcutAction);
            }
        }
    }

    private void executeDeleteFolderActionList(List<PresetShortcutAction> deleteFolderActionList) {
        if (!deleteFolderActionList.isEmpty()) {
            for (PresetShortcutAction presetShortcutAction : deleteFolderActionList) {
                executeDelete(presetShortcutAction);
            }
        }
    }

    private List<PresetShortcutAction> accessDeleteNotEqualsFolderActionList(List<PresetShortData> presetShortDataList) {
        synchronized (sBgLock) {
            List<PresetShortcutAction> presetShortcutActionList = new ArrayList<>();
            Context context = LauncherAppState.getInstance().getContext();

            //本地文件夹
            List<Long> localFolderList = new ArrayList<>();
            //本地文件夹和图标的对应关系
            Map<Long, List<String>> folderIdMapShortcutList = new HashMap<>();

            //处理本地数据库图标和文件夹的关系数据
            accessPresetFolder(context, localFolderList, folderIdMapShortcutList);

            for (PresetShortData presetShortData : presetShortDataList) {
                //本地文件夹和网络文件夹内容不一致
                List<String> deleteFolderList = folderIdMapShortcutList.get(presetShortData.folderId);
                if (deleteFolderList != null && !deleteFolderList.isEmpty()) {
                    for (String packageName : deleteFolderList) {
                        if (TextUtils.isEmpty(packageName)) {
                            Log.i("PresetShortcut", packageName + " packageName is epmty");
                            continue;
                        }

                        if (AppUtils.isAppInstalled(packageName)) {
                            Log.i("PresetShortcut", packageName + " is installed");
                            continue;
                        }

                        if (!existInPresetShortDataList(packageName, presetShortData.list)) {
                            boolean hit = false;
                            for (ShortcutInfo shortcutInfo : sPresetItemsIdList) {
                                if (shortcutInfo.getIntent() != null
                                        && shortcutInfo.getIntent().getComponent() != null
                                        && TextUtils.equals(packageName, shortcutInfo.getIntent().getComponent().getPackageName())) {
                                    hit = true;
                                    PresetShortcutAction presetShortcutAction = new PresetShortcutAction();
                                    presetShortcutAction.setDeleteItemInfo(shortcutInfo);
                                    presetShortcutAction.setFolderContainer(shortcutInfo.container);
                                    presetShortcutAction.setAction(PresetShortcutAction.ACTION_DELETE);
                                    presetShortcutAction.setPackageName(shortcutInfo.getIntent().getComponent().getPackageName());
                                    presetShortcutAction.setShortcutName(TextUtils.isEmpty(shortcutInfo.title) ? "" : shortcutInfo.title.toString());
                                    presetShortcutActionList.add(presetShortcutAction);
                                    break;
                                }
                            }
                            if (!hit) {
                                Log.e(TAG, "delete1 packageName:" + packageName);
                                context.getContentResolver().delete(
                                        PresetFolderProvider.PresetFolder.getDeleteShortcutUri(packageName), null, null
                                );
                            }
                        }
                    }
                }
            }
            return presetShortcutActionList;
        }
    }

    private List<PresetShortcutAction> accessDeleteFolderActionList(List<PresetShortData> presetShortDataList) {
        synchronized (sBgLock) {
            List<PresetShortcutAction> presetShortcutActionList = new ArrayList<>();
            Context context = LauncherAppState.getInstance().getContext();

            //本地文件夹
            List<Long> localFolderList = new ArrayList<>();
            //网络下发的文件夹
            List<Long> dispatchFolderList = new ArrayList<>();
            //本地文件夹和图标的对应关系
            Map<Long, List<String>> folderIdMapShortcutList = new HashMap<>();

            //处理本地数据库图标和文件夹的关系数据
            accessPresetFolder(context, localFolderList, folderIdMapShortcutList);

            for (PresetShortData presetShortData : presetShortDataList) {
                dispatchFolderList.add(presetShortData.folderId);
            }

            List<Long> deleteFolderList = accessDeleteFolderList(localFolderList, dispatchFolderList);
            List<String> deleteFolder;
            for (Long deleteFolderId : deleteFolderList) {
                deleteFolder = folderIdMapShortcutList.get(deleteFolderId);
                if (deleteFolder != null) {
                    for (String packageName : deleteFolder) {
                        if (TextUtils.isEmpty(packageName)) {
                            Log.i("PresetShortcut", packageName + " packageName is epmty");
                            continue;
                        }

                        if (AppUtils.isAppInstalled(packageName)) {
                            Log.i("PresetShortcut", packageName + " is installed");
                            continue;
                        }

                        boolean hit = false;
                        for (ShortcutInfo shortcutInfo : sPresetItemsIdList) {
                            if (shortcutInfo.getIntent() != null
                                    && shortcutInfo.getIntent().getComponent() != null
                                    && TextUtils.equals(packageName, shortcutInfo.getIntent().getComponent().getPackageName())) {
                                hit = true;
                                PresetShortcutAction presetShortcutAction = new PresetShortcutAction();
                                presetShortcutAction.setDeleteItemInfo(shortcutInfo);
                                presetShortcutAction.setFolderContainer(shortcutInfo.container);
                                presetShortcutAction.setAction(PresetShortcutAction.ACTION_DELETE);
                                presetShortcutAction.setPackageName(shortcutInfo.getIntent().getComponent().getPackageName());
                                presetShortcutAction.setShortcutName(TextUtils.isEmpty(shortcutInfo.title) ? "" : shortcutInfo.title.toString());
                                presetShortcutActionList.add(presetShortcutAction);
                                Log.e(TAG, "删除文件夹删除：" + shortcutInfo.title + "," + packageName);
                                break;
                            }
                        }
                        if (!hit) {
                            Log.e(TAG, "delete2 packageName:" + packageName);
                            context.getContentResolver().delete(
                                    PresetFolderProvider.PresetFolder.getDeleteShortcutUri(packageName), null, null
                            );
                        }
                    }
                }
            }

            return presetShortcutActionList;
        }
    }

    private boolean existInPresetShortDataList(String packageName, List<ResponeAppsShortcut> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (ResponeAppsShortcut responeAppsShortcut : list) {
            if (TextUtils.equals(packageName, responeAppsShortcut.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void executeDelete(final PresetShortcutAction presetShortcutAction) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                synchronized (sBgLock) {
                    String idByPackageName = DownloadManager.getImpl().getIdByPackageName(presetShortcutAction.getPackageName());
                    //如果正在下载，不做变动
                    if (!StringUtils.isEmpty(idByPackageName)) {
                        Log.i(TAG, presetShortcutAction.getShortcutName() + "is loading,don't action");
                        return;
                    }
                    Log.i(TAG, "executeDelete:" + presetShortcutAction.getDeleteItemInfo().title);
                    Context context = LauncherAppState.getInstance().getContext();
                    ShortcutInfo deleteItemInfo = (ShortcutInfo) presetShortcutAction.getDeleteItemInfo();
                    FolderInfo folderInfoByContainer = sBgFolders.get(presetShortcutAction.getFolderContainer());
                    deleteItemFromDatabaseDirect(context, deleteItemInfo);
                    if (folderInfoByContainer != null) {
                        folderInfoByContainer.remove(deleteItemInfo);
                    } else {
                        deleteDesktopShortcut(deleteItemInfo);
                    }
                }
            }
        });
    }

    private void executeAddNew(final PresetShortcutAction presetShortcutAction, final List<ShortcutInfo> shortcutInfoList,
                               final AtomicInteger loadPictureCounter, final int loadPictureSize) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                String idByPackageName = DownloadManager.getImpl().getIdByPackageName(presetShortcutAction.getPackageName());
                //如果正在下载，不做变动
                if (!StringUtils.isEmpty(idByPackageName)) {
                    Log.i(TAG, presetShortcutAction.getShortcutName() + "is loading,don't action");
                    return;
                }

                Log.i(TAG, "executeAddNew:" + presetShortcutAction.toString());
                final String folderName = presetShortcutAction.getTargetFolderName();
                final ShortcutInfo shortcutInfo = (ShortcutInfo) presetShortcutAction.getShortcutInfoFromResponse();

                Glide.with(mApp.getContext()).load(shortcutInfo.apkIconUrl)
                        .into(new SimpleTarget<Drawable>() {

                                  @Override
                                  public void onLoadFailed(@android.support.annotation.Nullable Drawable errorDrawable) {
                                      super.onLoadFailed(errorDrawable);
                                      loadPictureCounter.incrementAndGet();
                                      executeAddNewShortcutInfo(folderName, shortcutInfoList, loadPictureCounter, loadPictureSize);
                                  }

                                  @Override
                                  public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                      synchronized (sBgLock) {
                                          Context context = LauncherAppState.getInstance().getContext();
                                          final ArrayList<Long> addedWorkspaceScreensFinal = new ArrayList<Long>();
                                          ArrayList<Long> workspaceScreens = loadWorkspaceScreensDb(context);
                                          Pair<Long, int[]> coords;
                                          FolderInfo folderInfo;
                                          long containerId;
                                          coords = findSpaceForItemIfNeedNewScreen(context,
                                                  workspaceScreens, addedWorkspaceScreensFinal,
                                                  1, 1);
                                          if (TextUtils.isEmpty(folderName)) {//列表
                                              containerId = LauncherSettings.Favorites.CONTAINER_DESKTOP;
                                          } else {//文件夹
                                              folderInfo = getFolderByTitle(folderName);
                                              if (folderInfo != null) {
                                                  int[] folderCordinates = new int[]{folderInfo.cellX, folderInfo.cellY};
                                                  coords = Pair.create(folderInfo.screenId, folderCordinates);
                                                  containerId = folderInfo.id;
                                              } else {
                                                  Pair<Long, int[]> existFolderLocal = sFolderLocalMap.get(folderName);
                                                  if (existFolderLocal != null) {
                                                      coords = existFolderLocal;
                                                  } else {
                                                      sFolderLocalMap.put(folderName, coords);
                                                  }
                                                  containerId = LauncherSettings.Favorites.CONTAINER_DESKTOP;
                                              }

                                          }
                                          final Pair<Long, int[]> finalCoords = coords;
                                          final long finalContainerId = containerId;

                                          shortcutInfo.setIcon(((BitmapDrawable) resource).getBitmap());

                                          shortcutInfo.container = finalContainerId;
                                          shortcutInfo.screenId = finalCoords.first;
                                          shortcutInfo.cellX = finalCoords.second[0];
                                          shortcutInfo.cellY = finalCoords.second[1];

                                          loadPictureCounter.incrementAndGet();
                                          boolean add = true;
                                          for (ShortcutInfo presetShortcutInfo : sPresetItemsIdList) {
                                              if ((shortcutInfo.getIntent() == null || shortcutInfo.getIntent().getComponent() == null)
                                                      || (presetShortcutInfo.getIntent() != null && presetShortcutInfo.getIntent().getComponent() != null
                                                      && shortcutInfo.getIntent() != null && shortcutInfo.getIntent().getComponent() != null
                                                      && TextUtils.equals(presetShortcutInfo.getIntent().getComponent().getPackageName(), shortcutInfo.getIntent().getComponent().getPackageName()))) {
                                                  add = false;
                                              }
                                          }
                                          if (add) {
                                              addOrMoveItemInDatabaseById(
                                                      context, shortcutInfo,
                                                      shortcutInfo.container, shortcutInfo.screenId,
                                                      shortcutInfo.cellX, shortcutInfo.cellY
                                              );
                                              ContentValues contentValues = new ContentValues();
                                              contentValues.put(PresetFolderProvider.PresetFolder.PACKAGE_NAME, presetShortcutAction.getPackageName());
                                              context.getContentResolver().insert(
                                                      PresetFolderProvider.PresetFolder.getInsertUri(presetShortcutAction.getFolderId()), contentValues
                                              );
                                              shortcutInfoList.add(shortcutInfo);
                                          }
                                          executeAddNewShortcutInfo(folderName, shortcutInfoList, loadPictureCounter, loadPictureSize);
                                      }
                                  }
                              }
                        );
            }
        });
    }

    private void executeAddNewShortcutInfo(String folderName, List<ShortcutInfo> shortcutInfoList, AtomicInteger loadPictureCounter, int loadPictureSize) {
        if (loadPictureCounter.intValue() == loadPictureSize && !shortcutInfoList.isEmpty()) {
            Collections.sort(shortcutInfoList, new Comparator<ShortcutInfo>() {
                @Override
                public int compare(ShortcutInfo o1, ShortcutInfo o2) {
                    return o1.sortIndex - o2.sortIndex;
                }
            });
            for (ShortcutInfo shortcutInfo : shortcutInfoList) {
                addShortcutInfo(folderName, shortcutInfo);
            }
        }
    }

    private void executeModifyData(final PresetShortcutAction presetShortcutAction) {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                synchronized (sBgLock) {
                    String idByPackageName = DownloadManager.getImpl().getIdByPackageName(presetShortcutAction.getPackageName());
                    //如果正在下载，不做变动
                    if (!StringUtils.isEmpty(idByPackageName)) {
                        Log.i(TAG, presetShortcutAction.getShortcutName() + "is loading,don't action");
                        return;
                    }
                    Context context = LauncherAppState.getInstance().getContext();
                    ShortcutInfo existShortcutInfo = (ShortcutInfo) presetShortcutAction.getExistShortcutInfo();
                    ShortcutInfo shortcutInfoFromResponse = (ShortcutInfo) presetShortcutAction.getShortcutInfoFromResponse();
                    useNewShortcutInfoArgument(existShortcutInfo, shortcutInfoFromResponse);
                    FolderInfo folderInfo = sBgFolders.get(existShortcutInfo.container);
                    if (folderInfo != null) {
                        int index = folderInfo.contents.indexOf(existShortcutInfo);
                        if (index >= 0) {
                            folderInfo.contents.set(index, shortcutInfoFromResponse);
                        }
                    }
                    Log.i(TAG, "executeModifyData：" + shortcutInfoFromResponse.title);
                    addOrMoveItemInDatabaseById(
                            context, shortcutInfoFromResponse, shortcutInfoFromResponse.container,
                            shortcutInfoFromResponse.screenId, shortcutInfoFromResponse.cellX, shortcutInfoFromResponse.cellY
                    );
                    getCallback().bindPresetShortcutsChanged(shortcutInfoFromResponse);
                }
            }
        });
    }

    private void useNewShortcutInfoArgument(ShortcutInfo existShortcutInfo, ShortcutInfo shortcutInfoFromResponse) {
        shortcutInfoFromResponse.id = existShortcutInfo.id;
        shortcutInfoFromResponse.container = existShortcutInfo.container;
        shortcutInfoFromResponse.screenId = existShortcutInfo.screenId;
        shortcutInfoFromResponse.cellX = existShortcutInfo.cellX;
        shortcutInfoFromResponse.cellY = existShortcutInfo.cellY;
        shortcutInfoFromResponse.rank = existShortcutInfo.rank;
        shortcutInfoFromResponse.spanX = existShortcutInfo.spanX;
        shortcutInfoFromResponse.spanY = existShortcutInfo.spanY;
        shortcutInfoFromResponse.setIcon(existShortcutInfo.getIcon(mIconCache));
    }

    private void useExistShortcutInfoArgument(ShortcutInfo existShortcutInfo, ShortcutInfo shortcutInfoFromResponse) {
        existShortcutInfo.title = shortcutInfoFromResponse.title;
        existShortcutInfo.contentDescription = shortcutInfoFromResponse.contentDescription;
        existShortcutInfo.apkObsUrl = shortcutInfoFromResponse.apkObsUrl;
    }

    public void accessModifyOrAddActionList(List<PresetShortData> data) {
        synchronized (sBgLock) {
            Context context = LauncherAppState.getInstance().getContext();
            //生成最大集
            for (PresetShortData presetShortData : data) {
                if (presetShortData.list == null || presetShortData.list.isEmpty()) {
                    Log.i(TAG, "PresetShortcut folder " + presetShortData.folderName + " have no data");
                    continue;
                }

                List<PresetShortcutAction> modifyActionList = new ArrayList<>();
                List<PresetShortcutAction> addNewActionList = new ArrayList<>();

                PresetShortcutAction presetShortcutAction;
                ShortcutInfo shortcutInfoFromResponse;
                ShortcutInfo existShortcutInfo;
                for (ResponeAppsShortcut responeAppsShortcut : presetShortData.list) {
                    if (TextUtils.isEmpty(responeAppsShortcut.getPackageName())) {
                        Log.i("PresetShortcut", responeAppsShortcut.getAppName() + " packageName is epmty");
                        continue;
                    }

                    if (!isValidShortcut(responeAppsShortcut)) {
                        Log.i("PresetShortcut", responeAppsShortcut.getAppName() + " is invalied");
                        continue;
                    }

                    shortcutInfoFromResponse = getShortcutInfo(responeAppsShortcut, context);
                    existShortcutInfo = getItemFromSbItemsByPackageName(responeAppsShortcut.getPackageName());
                    presetShortcutAction = new PresetShortcutAction();
                    presetShortcutAction.setShortcutInfoFromResponse(shortcutInfoFromResponse);
                    presetShortcutAction.setTargetFolderName(presetShortData.folderName);
                    presetShortcutAction.setFolderId(presetShortData.folderId);
                    presetShortcutAction.setPackageName(responeAppsShortcut.getPackageName());
                    presetShortcutAction.setShortcutName(responeAppsShortcut.getAppName());
                    if (existShortcutInfo != null) {
                        presetShortcutAction.setFolderContainer(existShortcutInfo.container);
                        presetShortcutAction.setAction(PresetShortcutAction.ACTION_MODIFY_DATA);
                        presetShortcutAction.setSortIndex(responeAppsShortcut.getSortIndex());
                        modifyActionList.add(presetShortcutAction);
                    } else {
                        presetShortcutAction.setAction(PresetShortcutAction.ACTION_ADD_NEW);
                        presetShortcutAction.setSortIndex(responeAppsShortcut.getSortIndex());
                        addNewActionList.add(presetShortcutAction);
                    }
                    presetShortcutAction.setExistShortcutInfo(existShortcutInfo);
                }

                //执行修改
                executeModifyActionList(modifyActionList);
                //执行添加
                executeAddNewActionList(addNewActionList);
            }

        }
    }

    //找出需要删除的文件夹
    private List<Long> accessDeleteFolderList(List<Long> localFolderList, List<Long> dispatchFolderList) {
        List<Long> deleteFolderList = new ArrayList<>();
        for (Long localFolderId : localFolderList) {
            boolean hit = false;
            for (Long dispatchFolderId : dispatchFolderList) {
                if (localFolderId.equals(dispatchFolderId)) {
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                deleteFolderList.add(localFolderId);
            }
        }
        return deleteFolderList;
    }

    private void accessPresetFolder(Context context, List<Long> localFolderList, Map<Long, List<String>> folderIdMapShortcutList) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor query = contentResolver.query(PresetFolderProvider.PresetFolder.CONTENT_URI, null, null, null, null);
        long folderId;
        String packageName;
        try {
            while (query.moveToNext()) {
                folderId = query.getLong(query.getColumnIndex(PresetFolderProvider.PresetFolder.FOLDER_ID));
                List<String> packageNameList = folderIdMapShortcutList.get(folderId);
                if (packageNameList == null) {
                    packageNameList = new ArrayList<>();
                    folderIdMapShortcutList.put(folderId, packageNameList);
                    localFolderList.add(folderId);
                }
                packageName = query.getString(query.getColumnIndex(PresetFolderProvider.PresetFolder.PACKAGE_NAME));
                packageNameList.add(packageName);
            }
        } finally {
            if (query != null) {
                query.close();
            }
        }
    }

    private boolean hitShortcutInfo(ShortcutInfo shortcutInfo, ItemInfo itemInfo) {
        return itemInfo != null && itemInfo.getIntent() != null &&
                TextUtils.equals(shortcutInfo.getIntent().getPackage(), itemInfo.getIntent().getPackage());
    }

    public static ShortcutInfo getItemFromSbItemsByPackageName(String packageName) {
        synchronized (sBgLock) {
            for (ItemInfo itemInfo : sBgItemsIdMap) {
                if (itemInfo instanceof ShortcutInfo) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                    if (shortcutInfo.getIntent() != null && TextUtils.equals(shortcutInfo.getIntent().getPackage(), packageName)) {
                        return shortcutInfo;
                    }
                }
            }
            return null;
        }
    }

    public static FolderInfo getFolderByTitle(String title) {
        synchronized (sBgLock) {
            for (FolderInfo folderInfo : sBgFolders) {
                if (TextUtils.equals(folderInfo.title, title)) {
                    return folderInfo;
                }
            }
            return null;
        }
    }

    public void channelIcon(ChannelIconInfo iconInfo) {
        if (iconInfo.channelIcons == null || iconInfo.channelIcons.isEmpty()) {
            return;
        }
        UserHandleCompat myUser = UserHandleCompat.myUserHandle();
        Set<String> hideSet = SPUtils.getInstance().getStringSet("ddy_channel_icon", new HashSet<String>());
        for (ChannelIconInfo.DataBean dataBean : iconInfo.channelIcons) {
            if (AppUtils.isAppInstalled(dataBean.pkgName)) {
                if (dataBean.hideMarket) {
                    hideSet.add(dataBean.pkgName);
                    ((Launcher) getCallback()).getWorkspace().removeAbandonedPromise(dataBean.pkgName, myUser);
                }
            }
        }
        //重启重新出现，暂不保存设置，故注释
//        if (!hideSet.isEmpty()) {
//            SPUtils.getInstance().put("ddy_channel_icon", hideSet);
//        }
    }

}
