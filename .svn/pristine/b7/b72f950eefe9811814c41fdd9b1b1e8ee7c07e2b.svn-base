package com.android.launcher3.arrange;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;

/**
 * Created by suchangxu.
 * Date: 2020/7/16 10:53
 */
public class ArrangeReceiver extends BroadcastReceiver {

    private static final String TAG = "ArrangeReceiver";

    public static final String ACTION = "com.cyjh.huawei.launcher3.arrangeDesktop";

    public static final String KEY = "sortTable";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && TextUtils.equals(intent.getAction(), ACTION)) {
            Log.i(TAG, "onReceive: start arrange desktop");
            String sortTable = intent.getStringExtra(KEY);
            if (TextUtils.isEmpty(sortTable)) {
                Log.e(TAG, "sort data is empty");
                return;
            }
            Log.i(TAG, "sort data:" + sortTable);
            LauncherModel model = LauncherAppState.getInstance().getModel();
            model.arrangeDesktop(sortTable);
//            MockInstaller.getInstance().mockInstall("/data/local/launcher_mock/zhihu.apk");
//            MockInstaller.getInstance().mockInstall("/data/local/launcher_mock/wangxin_android.apk");
//            MockInstaller.getInstance().mockInstall("/data/local/launcher_mock/taobao.apk");

//            LauncherAppState.getLauncherProvider().delete(LauncherSettings.Favorites.CONTENT_URI, "title=?", new String[]{"飞机喊话示例"});
        }
    }

}
