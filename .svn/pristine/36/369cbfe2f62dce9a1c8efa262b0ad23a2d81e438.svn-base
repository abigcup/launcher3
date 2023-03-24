Launcher3
=========

更改官方的Launcher3使得可以在Android Studio编译

* [原始地址](https://android.googlesource.com/platform/packages/apps/Launcher3/)，从5892520提交开始
* 最小sdk版本为16
* Android 5.0 版本及以上可能会出现因为相同权限声明而不能安装的问题
* 如果你想要可以在Eclipse编译的版本，可以看这个tag: GOODBYE_ECLIPSE


---
我写了几篇博客来讲解Launcher3桌面什么的，有兴趣的可以看看这里：

* [概述](http://www.fookwood.com/archives/788)
* [Android的触摸控制流程](http://www.fookwood.com/archives/806)
* [Launcher3界面的布局和结构](http://www.fookwood.com/archives/846)
* [Launcher3中的常用类](http://www.fookwood.com/archives/854)
* [Launcher3的启动流程（一）](http://www.fookwood.com/archives/863)
* [细说ItemInfo](http://www.fookwood.com/archives/875)
* [Launcher3的启动流程（二）](http://www.fookwood.com/archives/894)
* [Launcher3分析之拖动图标的流程——按下](http://www.fookwood.com/archives/925)
* [Launcher3分析之拖动图标的流程——移动](http://www.fookwood.com/archives/940)
* [Launcher3分析之拖动图标的流程——放下](http://www.fookwood.com/archives/946)
* [PagedView的原理 – 滑动](http://www.fookwood.com/archives/955)
* [如何给Launcher3添加左屏](http://www.fookwood.com/archives/1048)
* [IconCache原理](http://www.fookwood.com/archives/1072)
* [找个Launcher开发](http://www.fookwood.com/archives/1066)
* [LauncherRootView和DragLayer的布局过程](http://www.fookwood.com/archives/1085)

# 桌面图标配置：图标圆角开关、圆角大小、图标背景开关、图标背景颜色、图标替换开关
* 时间：2021.12.14
* 版本：2.2.107 
* 未配置时的默认值，如下：
com.android.launcher3.bean.DesktopIconInfo
```json
{
  "enableIconRoundCorner": false,
  "iconRoundCornerSize": 25,
  "enableIconBackgroundColor": false,
  "iconBackgroundColor": "#FFFFFF",
  "enableIconReplace": false,
  "appsdetails": [
    {
      "pkg": "ru.zdevs.zarchiver.pro",
      "icon": "/data/local/tmp/desktop/ru.zdevs.zarchiver.pro.png",
      "appname": "文件神器"
    },
    {
      "pkg": "com.cyjh.appmarket",
      "icon": "",
      "appname": "我的市场"
    }
  ]
}
```
* 配置文件路径为：
/data/local/setting/desktop/icon_config.json

* 图标替换
enableIconReplace为true时，检查路径/data/local/tmp/desktop/包名.png，有相应的图标会进行替换

# 修改图标大小
* 代码位置：InvariantDeviceProfile#getPredefinedDeviceProfiles() // 配置默认图标大小

# 快捷图标栏文本显示 
* 代码位置：CellLayout#addViewToCellLayout

# 快捷图标栏边距
* xml属性配置为：dynamic_grid_hotseat_margin

# 配置页面指示器高度
* xml属性配置为：dynamic_grid_page_indicator_height

# 图标与文字间距
* xml属性配置为：dynamic_grid_icon_drawable_padding

```java
// /data/local/setting/desktop/icon_config.json 新增如下配置
// DesktopIconInfo.java
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
    public boolean hotseatShowText = false;

    /**
     * 页面指示器高度
     */
    public float pageIndicatorHeight = 20;

    /**
     * 图标与文字间距
     */
    public float iconDrawablePadding = 4;
```




