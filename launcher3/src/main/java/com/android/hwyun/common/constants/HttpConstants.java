package com.android.hwyun.common.constants;

import com.android.hwyun.common.util.DomainUtils;

/**
 * Created by xuwei on 2018/9/11.
 */
public class HttpConstants {

    /**
     * 批量安装回执，回执通知安装任务的执行结果
     */
    public static final String BATCH_INSTALL_RECEIPT = DomainUtils.API_BASE_URL_DDY + "/App/BatchInstallReceipt";
    /**
     * 保存应用启动数据
     */
    public static final String APPMARKET_SAVE_APPSTARTUPDATA = DomainUtils.API_BASE_URL_DDY + "/AppMarket/SaveAppStartUpData";
    /**
     * 快捷方式应用关联应用
     */
    public static final String APPMARKET_ASSOCIATED_APPS_SHORTCUT = DomainUtils.API_BASE_URL_DDY + "/AppMarket/AssociatedAppsShortcut";

    /**
     * 快捷方式应用
     */
    public static final String APPMARKET_APPS_SHORTCUT = DomainUtils.API_BASE_URL_DDY + "/AppMarket/AppsShortcut";

    /**
     * Toast应用列表和图片对象
     */
    public static final String APPMARKET_APPS_TOASTAPPS = DomainUtils.API_BASE_URL_DDY + "/AppMarket/ToastApps";

    /**
     * 应用启动
     */
    public static final String APPMARKET_SAVE_APPS_START = DomainUtils.API_BASE_URL_DDY + "/AppMarket/SaveAppStartUpData";
    /**
     * 应用安装
     */
    public static final String APPMARKET_SAVE_APPS_INSTALL = DomainUtils.API_BASE_URL_DDY + "/AppMarket/SaveAppInstallData";

    /**
     * 后台进程统计
     */
    public static final String APPMARKET_CLOUND_DEVICE_PROCESS_DATA_COLLECT = DomainUtils.API_BASE_URL_DDY + "/AppMarket/CloudDeviceProcessDataCollect";

    /**
     * 获取Web端设置的过滤列表
     */
    public static final String APPMARKET_CLOUND_REQUEST_FILTER_WORD = DomainUtils.API_BASE_URL_DDY + "/App/FitlerChannelWord";

    /**
     * 设备号查订单号和设备组
     */
    public static final String api_deviceorder = DomainUtils.API_BASE_URL_DDY + "/Device/Order";

    public static final String API_BuriedPoint = DomainUtils.API_BASE_URL_DATA + "/api/BuriedPoint";

    /**
     * obs参数获取
     */
    public static final String obs_creatcert = DomainUtils.API_BASE_URL_OBS + "/api/CreatCert";
    /**获取用户预置应用列表*/
    public static final String APP_PRESET_APP = DomainUtils.API_BASE_URL_DDY + "/CommonSDK/CommonSDKPresetApp";
    /**文件下载*/
    public static final String FILE_DOWN =DomainUtils.API_BASE_URL_STORAGE+ "/api/FileDown";
}
