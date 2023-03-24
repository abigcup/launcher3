package com.android.hwyun.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.Utils.GsonUtil;
import com.android.hwyun.common.constants.CommonConstants;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.bean.PresetShortData;
import com.blankj.utilcode.util.EncodeUtils;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * Created by suchangxu.
 * Date: 2020/8/10 10:17
 */
public class PresetBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PresetBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.equals(intent.getAction(), CommonConstants.CTJH_ACTION_PRESET)) {
            return;
        }

        String presetData = intent.getStringExtra(CommonConstants.EXTRA_KEY_PRESET_DATA);
        Log.i(TAG, "preset data:" + presetData);
        if (TextUtils.isEmpty(presetData)) {
            return;
        }

        try {
            presetData = new String(EncodeUtils.base64Decode(presetData), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "generate string error");
            e.printStackTrace();
        }

        Log.i(TAG, "decode data:" + presetData);

        Type type = new TypeToken<List<PresetShortData>>() {
        }.getType();
        List<PresetShortData> presetShortDataList = GsonUtil.fromJson(presetData, type);

        if (presetShortDataList == null) {
            Log.e(TAG, "data parse error");
            return;
        }

        LauncherModel model = LauncherAppState.getInstance().getModel();
        model.loadPresetShortcutData(presetShortDataList);


    }
}
