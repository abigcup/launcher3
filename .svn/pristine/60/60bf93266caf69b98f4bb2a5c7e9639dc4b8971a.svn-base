package com.android.hwyun.statistics;

import com.android.launcher3.BuildConfig;

/**
 * Created by lijingying on 2019/3/19.
 *  统计相关常量。
 *  web接口：http://data.ddyun123.com/Help/Api/POST-api-BuriedPoint
 */
public class StatisticsConstants {
    /**首页加载     ID范围：1-100
    路径：客户端启动次数（使用app/ads接口调用次数）——首页加载次数（加载至首页时上传并记录）
    */
    public static final int  FisrtPage_AdLauncher = 1;
    public static final int  FisrtPage_MainLoaded = 2;


    /**试用转化     ID范围：101-200
    路径：注册页访问设备数（统计IMEI）——单日注册用户数——当日注册用户试用领取数——连接建立用户数（忽略订单类型）
     ——当日累计连接时长大于1分钟用户数——当日累计连接时长大于5分钟用户数
     ——有安装记录用户数——有启动记录用户数——当日购买用户数
    */
    public static final int UserTran_InRegistUI =   101;
//    public static final int UserTran_Regist =   102;
//    public static final int UserTran_NewWelFare =   103;
    public static final int UserTran_MediaConnected =   104;
    public static final int UserTran_MediaConnBeyond1Min =   105;
    public static final int UserTran_MediaConnBeyond5Min =   106;
//    public static final int UserTran_AppInstall =   107;
//    public static final int UserTran_AppStart =   108;
//    public static final int UserTran_OrderBuy =   109;


    /**搜索结果满意度      ID范围:201-250
    路径：应用市场启动数——发起搜索数（按应用市场首页搜索栏点击次数）——搜索结果有效用户数
     PS:有效结果为点击记录事件为“渠道选择/打开/安装”；
    */
    public static final int MarketSearchResult_Launcher =   201;
    public static final int MarketSearchResult_Searched =   202;
    public static final int MarketSearchResult_SearchedEffect =   203;


    /**关联下载记录       ID范围:251-300
     路径：应用市场应用下载次数——关联应用下载次数
    */
    public static final int MarketAppDownload_App=   251;
    public static final int MarketAppDownload_AssociationApp =   252;
}
