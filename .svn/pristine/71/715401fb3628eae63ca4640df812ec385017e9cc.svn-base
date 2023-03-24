package com.android.launcher3.bean;

import java.util.List;

/**
 * 桌面图标配置：图标圆角开关、圆角大小、图标背景颜色
 *
 * @author tomchen
 * @date 12/14/21
 */
public class DesktopIconInfo {

    /**
     * 图标圆角开关
     */
    public boolean enableIconRoundCorner = false;

    /**
     * 圆角大小：enableIconRoundCorner为true时生效
     */
    public int iconRoundCornerSize = 25;

    /**
     * 图标背景开关
     */
    public boolean enableIconBackgroundColor = false;

    /**
     * 图标背景颜色：enableBackgroundColor为true时生效
     */
    public String iconBackgroundColor = "#FFFFFF";

    /**
     * 图标替换开关
     */
    public boolean enableIconReplace = false;

    /**
     * 应用图标、名称配置列表
     */
    public List<AppsdetailsBean> appsdetails;

    public static class AppsdetailsBean {
        public String pkg;
        public String icon;
        public String appname;
    }

    // "iconSize":64,"iconTextSize":13,"hotseatIconSize":64,"hotseatShowText":true,"hotseatBarMargin":43,
    // 配置桌面图标大小、文字大小
    /**
     * 桌面图标大小
     */
    public float iconSize = 64;

    /**
     * 桌面文字大小
     */
    public float iconTextSize = 13;

    // 底部快捷图标大小、文字是否显示、底部边距
    /**
     * 快捷图标大小
     */
    public float hotseatIconSize = 64;

    /**
     * 快捷图标栏底部边距
     */
    public float hotseatBarMargin = 43;

    /**
     * 快捷图标是否显示文本
     */
    public boolean hotseatShowText = true;

    /**
     * 页面指示器高度
     */
    public float pageIndicatorHeight = 20;

    /**
     * 图标与文字间距
     */
    public float iconDrawablePadding = 4;

    /**
     * 保持图标原大小，不进行缩放
     */
    public boolean keepIconSize;

}
