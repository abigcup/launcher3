package com.android.hwyun.prevshortcut;

import android.view.View;

import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;
import com.android.hwyun.common.bean.XBYUserInfo;
import com.android.launcher3.bean.PresetShortData;

import java.util.List;

/**
 * Created by lijingying on 2019/1/8.
 * 对内提供指定应用的下载快捷图标的：创建、启动、删除 接口
 */
public class PrevShortcutContract {
    public interface InitPresent {
        void process(List<String> appPackageList, boolean bRecommendMsg, XBYUserInfo userInfo);

        void init(DetailPresent p);
    }

    public interface DetailPresent {
        //该接口会自动去重，并把之前已不存在的删除掉
        void create(final List<ResponeAppsShortcut> apps);

        void add(final ResponeAppsShortcut app);

        void delete(final ResponeAppsShortcut app);

        /**
         * 获取预置快捷方式数据，包括文件夹和列表两种数据。
         * 列表模式服用了create()方法
         */
        void loadPresetShortcutData(List<PresetShortData> presetShortDataList);

        //下载快捷方式存在的情况下，尝试触发下载
        void startDownload(final ResponeAppsShortcut app);

        void saveDownloadInfo(final Long Channeld, View view);
    }
}
