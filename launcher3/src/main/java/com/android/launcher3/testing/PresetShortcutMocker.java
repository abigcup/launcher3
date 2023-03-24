package com.android.launcher3.testing;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.Utils.GsonUtil;
import com.android.hwyun.broadcastreceiver.PresetBroadcastReceiver;
import com.android.hwyun.common.constants.CommonConstants;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.bean.PresetShortData;
import com.blankj.utilcode.util.EncodeUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.IllegalCharsetNameException;
import java.util.List;

/**
 * Created by suchangxu.
 * Date: 2020/7/21 15:59
 * 快捷方式预置模拟数据
 */
public class PresetShortcutMocker {

    private static final String MOCK_DATA1 = "[{\"folderId\":1,\"folderName\": \"文件夹1\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset1\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji1\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]}]";
    private static final String MOCK_DATA2 = "[]";
    //两个文件夹
    private static final String MOCK_DATA3 = "[{\"folderId\":1,\"folderName\": \"文件夹1\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset1\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji1\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]},{\"folderId\":2,\"folderName\": \"文件夹2\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset2\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji2\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]}]";
    private static final String MOCK_DATA4 = "[{\"folderId\":1,\"folderName\": \"文件夹1\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset1\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji1\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]},{\"folderId\":2,\"folderName\": \"文件夹2\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset2\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji2\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]},{\"folderId\":0,\"folderName\": \"\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置3\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset3\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机3\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji3\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]}]";

    //MOCK_DATA4->MOCK_DATA5    三文件夹变两文件夹，且应用有重叠
    private static final String MOCK_DATA5 = "[{\"folderId\":2,\"folderName\": \"文件夹2\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset2\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji2\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"预置1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset1\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji1\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]},{\"folderId\":0,\"folderName\": \"\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置3\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset3\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机3\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji3\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]}]";

    //MOCK_DATA4->MOCK_DATA6   两文件夹变一文件夹，有重叠，单图标不要
    private static final String MOCK_DATA6 = "[{\"folderId\":2,\"folderName\": \"文件夹2\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset2\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji2\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"预置1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset1\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji1\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]}]";

    //MOCK_DATA4->MOCK_DATA7  两文件夹变一文件夹，有重叠，有添加，单图标不要
    private static final String MOCK_DATA7 = "[{\"folderId\":2,\"folderName\": \"文件夹2\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置2\",\"AppImgUrl\": \"https://sdk.7e9b6b7f420fe644.obs.cn-east-2.myhuaweicloud.com/2020/04/07/317f657383c045a89677a84637f6b574.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset2\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji2\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"预置1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset1\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"飞机1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.presetfeiji1\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"新增1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.addnew1\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"新增2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.addnew2\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]}]";

    private static final String MOCK_DATA8 = "[{\"folderId\":1,\"folderName\": \"文件夹1\",\"list\": [{\"Id\": 1153,\"AppName\": \"预置3\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1677,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset3\",\"AttachPackageName\": \"\",\"ApkObsUrl\": \"App_Data/Game_App/cn.mobage.g12000128.a360/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 3,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"预置4\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset4\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 4,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"预置2\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset2\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 2,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"预置5\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset5\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 5,\"AppSort\": 0},{\"Id\": 1153,\"AppName\": \"预置1\",\"AppImgUrl\": \"http://res.ddyun.com/Img/2019/12/23/e7caf4d4189b4daca0a6425bce51fca8.png\",\"AppType\": 1,\"Channeld\": 1678,\"ChannelName\": \"官网\",\"AssociatedDesc\": null,\"MD5\": \"1\",\"AppVerId\": 206,\"PackageName\": \"com.cyjh.preset1\",\"AttachPackageName\": \"\",\"ApkObsUrl\":\"App_Data/Game_App/com.cyjh.voice2nd/base.apk\",\"ApkPath\": null,\"AppSource\": 1,\"SortIndex\": 1,\"AppSort\": 0}]}]";


    public static final class InstanceHolder {

        private static final PresetShortcutMocker INSTANCE = new PresetShortcutMocker();

    }

    public static PresetShortcutMocker getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void presetDirectory(Context context, int index) {

        byte[] bytes;
        Intent intent = new Intent(CommonConstants.CTJH_ACTION_PRESET);
        if (index == 1) {
            bytes = EncodeUtils.base64Encode(MOCK_DATA4);
        } else {
            bytes = EncodeUtils.base64Encode(MOCK_DATA7);
        }
        String base64Str = new String(bytes);
        Log.i("base64编码后：", base64Str);
        intent.putExtra(CommonConstants.EXTRA_KEY_PRESET_DATA, base64Str);

        context.sendBroadcast(intent);

    }

}