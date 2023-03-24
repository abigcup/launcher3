package com.android.hwyun.common.bean;

import com.android.hwyun.common.constants.ConstantsKey;
import com.android.hwyun.common.constants.SharedPreferencesConstants;
import com.android.hwyun.common.util.AppUtil;
import com.android.hwyun.common.util.SharepreferenceUtils;
import com.android.launcher3.Application.MainApplication;
import com.android.launcher3.util.SignUtils;

/**
 * Created by chenmingqun on 2018/7/19.
 */

public class RequestBase {

    /**
     * UCID : 1
     * AppId : sample string 1
     * ChannelName : sample string 2
     * AppVersion : sample string 3
     * AppVersionCode : 4
     * IMEI : sample string 5
     */

    public String UCID = MainApplication.getInstance().getUCID();
    public String AppId = ConstantsKey.APP_ID;
    public String ChannelName = MainApplication.getInstance().getShieldChannel();//"ddy";
    public String AppVersion = AppUtil.getVersionName(MainApplication.getInstance(), MainApplication.getInstance().getPackageName());
    public int AppVersionCode = AppUtil.getVersionCode(MainApplication.getInstance(), MainApplication.getInstance().getPackageName());
    public String IMEI = AppUtil.getIMEI(MainApplication.getInstance()) == null ? SharepreferenceUtils.getSharedPreferencesToString(SharedPreferencesConstants.KEY_UUID, "123456789") : AppUtil.getIMEI(MainApplication.getInstance());
//    public String ShieldChannel = BaseApplication.getInstance().getShieldChannel();
    public int DDYAppVersionCode = MainApplication.getInstance().getDDYVercode();
    public String AppSign = SignUtils.getAppSignatureSHA1();

}
