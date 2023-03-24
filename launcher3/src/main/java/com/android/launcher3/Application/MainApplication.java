package com.android.launcher3.Application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.Utils.ShellUtils2;
import com.android.hwyun.common.util.DomainUtils;
import com.android.hwyun.common.util.UserInfoUtil;
import com.android.launcher3.BuildConfig;
import com.android.launcher3.R;
import com.android.launcher3.migratedb.DBMigrateHelper;
import com.android.launcher3.util.HotSeatMgr;
import com.android.launcher3.util.ProcessUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.Utils;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.IFileDownloadServiceProxy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


/**
 * Created by HuangJie on 2017/12/14.
 */

public class MainApplication extends Application {

    private static MainApplication ourInstance = new MainApplication();
    private static Context mContext;

    public static MainApplication getInstance() {
        return ourInstance;
    }

    public static Context getContext() {
        return mContext;
    }

    public static Handler fistScreenViewHandler;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = this;
        DBMigrateHelper.getInstance().migrate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("starttime", "MainApplication onCreate");  //0
        ourInstance = this;

        Log.i("starttime", "MainApplication onCreate 1 CrashReport");
        CrashReport.initCrashReport(getApplicationContext(), "f5917470cb", BuildConfig.DEBUG);   //100ms

        Log.i("starttime", "MainApplication onCreate 2 Utils");
        Utils.init(this);

        Log.i("starttime", "MainApplication onCreate 3 FileDownloader");
        FileDownloader.setup(mContext);

        //HTTPDNS要早于友盟初始化，否则accountid会错误，https://help.aliyun.com/knowledge_detail/58422.html?spm=a2c4g.11186623.4.4.7c674c07HGzLia  第7点
        DomainUtils.initHTTPDNS(this);

        Log.i("starttime", "MainApplication onCreate 4 Logger");
        Logger.addLogAdapter(new DiskLogAdapter());
//        LogUtils.getConfig().setLog2FileSwitch(true);

        Log.i("starttime", "MainApplication onCreate 5 HWYunManager"); //190ms
        //HWYunManager.getInstance().init();

        Log.i("starttime", "MainApplication onCreate 6 ProcessUtils");   //390ms
        ProcessUtils.ProcessSetting processSetting = ProcessUtils.getProcessSetting();
        if (processSetting.enable){
            String randomProcess = ProcessUtils.getRandomProcess(5);
            ProcessUtils.initProcess(randomProcess);
            processSetting.appName = getString(R.string.app_name);
            processSetting.packageName = getPackageName();
            processSetting.processName = randomProcess;
        }
        ProcessUtils.recordProcessName(processSetting);
        Log.i("starttime", "MainApplication onCreate end.");

        initUninstallBlackList();

//        try {
//            HotSeatMgr.getInstance().init();
//        } catch (IOException | XmlPullParserException e) {
//            e.printStackTrace();
//        }
    }

    private void initUninstallBlackList() {
        FileUtils.createOrExistsFile("/data/local/config/UninstallBlacklist");
        ShellUtils2.execCmd("chmod 777 /data/local/config/UninstallBlacklist", true);
    }

    public String getShieldChannel() {
//        String channelName = "";
//        String channelFilePath = new File(Environment.getExternalStorageDirectory(), PathConstants.APP_CHANNEL_FILE).getPath();
//        if (FileUtils.isFileExists(channelFilePath)) {
//            channelName = FileIOUtils.readFile2String(channelFilePath);
//            if (!TextUtils.isEmpty(channelName)) {
//                SPUtils.getInstance().put(SharedPreferencesConstants.KEY_APP_CHANNEL, channelName);
//                Log.e("BaseApplication", "getShieldChannel: " + channelName);
//            } else {
//                String channelNameSP = SPUtils.getInstance().getString(SharedPreferencesConstants.KEY_APP_CHANNEL);
//                if (!TextUtils.isEmpty(channelNameSP)) {
//                    channelName = channelNameSP;
//                }
//            }
//        }

        String channelName = UserInfoUtil.getChannel();
        return TextUtils.isEmpty(channelName) ? "ddy" : channelName;
    }

    public int getDDYVercode() {
        return UserInfoUtil.getDDYVercode();
    }

    public String getUCID() {
        return UserInfoUtil.getUcid();
    }
}