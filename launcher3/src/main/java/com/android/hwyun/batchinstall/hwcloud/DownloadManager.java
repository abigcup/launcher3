package com.android.hwyun.batchinstall.hwcloud;

import android.os.AsyncTask;

import com.android.hwyun.batchinstall.bean.DownloadFileInfo;
import com.android.hwyun.common.util.DownloadUtil;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ShortcutInfo;
import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuwei on 2018/8/29.
 */
public class DownloadManager {
    private Map<String, DownloadTask> downloadTasks;
    private Map<String, DownloadViewHolder> viewHolderMap;
    private Map<String, String> packageNameMap;
    private List<OnDownloadListener> downloadListeners;
    private OnDownloadListenerBase downloadListenerBase; //通知每一项ViewHolder界面变化
    private int progressInterval = 500; //进度通知时间间隔毫秒

    private final static class HolderClass {
        private final static DownloadManager INSTANCE
                = new DownloadManager();
    }

    public static DownloadManager getImpl() {
        return HolderClass.INSTANCE;
    }

    private DownloadManager() {
        downloadTasks = new HashMap<>();
        packageNameMap = new HashMap<>();
        viewHolderMap = new HashMap<>();
        downloadListeners = new ArrayList<>();
        downloadListenerBase = new OnDownloadListenerBase();
    }

    public void resetViewHolder(String id, String obsObjectKey, String savePath, DownloadViewHolder viewHolder) {
        String downloadId = viewHolder.getDownloadID();
        if (!StringUtils.isEmpty(downloadId) && !downloadId.equals(id)) {
            packageNameMap.remove(((ShortcutInfo) ((BubbleTextView) viewHolder).getTag()).getIntent().getPackage());
            viewHolderMap.remove(id);
        }
    }

    public void updateViewHolder(String downloadID, DownloadViewHolder viewHolder) {
        viewHolder.setDownloadID(downloadID);
        viewHolderMap.put(downloadID, viewHolder);
        packageNameMap.put(((ShortcutInfo) ((BubbleTextView) viewHolder).getTag()).getIntent().getPackage(), downloadID);
    }

    public String getIdByPackageName(String packageName) {
        String longId = packageNameMap.get(packageName);
        if (StringUtils.isEmpty(longId)) {
            return "";
        }
        return longId;
    }

    public void download(long id, String obsObjectKey, String savePath, DownloadViewHolder viewHolder, ObsCert obsCert) {
        download(new DownloadFileInfo(obsObjectKey, id+"", savePath), viewHolder, obsCert);
    }

    public void download(DownloadFileInfo downloadFileInfo, DownloadViewHolder viewHolder, ObsCert obsCert) {
//        int id = DownloadUtil.generateId(obsObjectKey, savePath);
        DownloadTask task = new DownloadTask(obsCert)
                .setDownloadFileInfo(downloadFileInfo)
                .setDownloadListener(downloadListenerBase)
                .setProgressInterval(progressInterval);
        downloadTasks.put(downloadFileInfo.getDownloadID(), task);
        if (viewHolder != null) {
            viewHolder.setDownloadID(downloadFileInfo.getDownloadID());
            viewHolderMap.put(downloadFileInfo.getDownloadID(), viewHolder);
            packageNameMap.put(((ShortcutInfo) ((BubbleTextView) viewHolder).getTag()).getIntent().getPackage(), downloadFileInfo.getDownloadID());
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadFileInfo.getObsObjectKey(), downloadFileInfo.getSavePath());
    }

    public boolean isDownloading(long id) {
        return downloadTasks.get(id) != null;
    }

    public DownloadFileInfo getDownloadFileInfo(long id, String obsObjectKey, String savePath) {
//        int id = DownloadUtil.generateId(obsObjectKey, savePath);
        if (isDownloading(id)) {
            return downloadTasks.get(id).getDownloadFileInfo();
        } else {
            return null;
        }
    }

    public class OnDownloadListenerBase implements OnDownloadListener {
        @Override
        public void progress(DownloadFileInfo downloadFileInfo) {
            DownloadViewHolder viewHolder = viewHolderMap.get(downloadFileInfo.getDownloadID());
            if (viewHolder != null) {
                viewHolder.updateProgress(downloadFileInfo);
            }
            for (OnDownloadListener listenerBase : downloadListeners) {
                listenerBase.progress(downloadFileInfo);
            }
        }

        @Override
        public void downloadCompleted(DownloadFileInfo downloadFileInfo) {
            DownloadViewHolder viewHolder = viewHolderMap.get(downloadFileInfo.getDownloadID());
            if (viewHolder != null) {
                viewHolder.downloadSuccess(downloadFileInfo);
            }
            for (OnDownloadListener listenerBase : downloadListeners) {
                listenerBase.downloadCompleted(downloadFileInfo);
            }
        }

        @Override
        public void installCompleted(DownloadFileInfo downloadFileInfo) {
            DownloadViewHolder viewHolder = viewHolderMap.get(downloadFileInfo.getDownloadID());
            if (viewHolder != null) {
                viewHolder.installSuccess(downloadFileInfo);
                viewHolderMap.remove(downloadFileInfo.getDownloadID());
                Collection<String> values = packageNameMap.values();
                while (values.contains(downloadFileInfo.getDownloadID())) {
                    values.remove(downloadFileInfo.getDownloadID());
                }
            }
            for (OnDownloadListener listenerBase : downloadListeners) {
                listenerBase.installCompleted(downloadFileInfo);
            }
            downloadTasks.remove(downloadFileInfo.getDownloadID());
        }

        @Override
        public void error(DownloadFileInfo downloadFileInfo, int errorCode, String errorMessage) {
            DownloadViewHolder viewHolder = viewHolderMap.get(downloadFileInfo.getDownloadID());
            if (viewHolder != null) {
                viewHolder.downloadError(errorCode);
                viewHolderMap.remove(downloadFileInfo.getDownloadID());
                Collection<String> values = packageNameMap.values();
                while (values.contains(downloadFileInfo.getDownloadID())) {
                    values.remove(downloadFileInfo.getDownloadID());
                }
            }
            for (OnDownloadListener listenerBase : downloadListeners) {
                listenerBase.error(downloadFileInfo, errorCode, errorMessage);
            }
            downloadTasks.remove(downloadFileInfo.getDownloadID());
        }
    }

    public void addDownloadListener(OnDownloadListener listenerBase) {
        downloadListeners.add(listenerBase);
    }

    public void removeDownloadListener(OnDownloadListener listenerBase) {
        downloadListeners.remove(listenerBase);
    }
}
