package com.android.hwyun.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.Utils.GsonUtil;
import com.android.hwyun.batchinstall.Presenter.BatchInstallPresenter;
import com.android.hwyun.batchinstall.bean.request.InstallApkCommandRequest;
import com.android.hwyun.batchinstall.bean.request.InstallApksCommandRequest;
import com.android.hwyun.batchinstall.contract.BatchInstallContract;
import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.prevshortcut.PrevShortcutContract;
import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;
import com.android.hwyun.prevshortcut.bean.UserLocalInstalledAppInfoRequest;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.bean.ArrangeDesktopInfo;
import com.android.launcher3.bean.ChannelIconInfo;
import com.android.launcher3.compat.UserHandleCompat;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.ddy.httplib.JsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lijingying on 2019/1/7.
 * 来自辅助通知过来的广播
 */
public class HWBroadcastReceiver extends BroadcastReceiver {
    //批量安装
    private BatchInstallContract.IPresenter batchInstallPresenter = new BatchInstallPresenter();

    //下载快捷方式
    private PrevShortcutContract.InitPresent prevShortcutPresenter;

    private abstract class Process {
        abstract void process(final String jsonMsg);
    }

    private List<Process> processList = new ArrayList<>();

    public HWBroadcastReceiver() {
        //快速安装
        processList.add(new Process() {
            @Override
            void process(String jsonMsg) {
                InstallApkCommandRequest commandRequest =
                        (InstallApkCommandRequest) JsonUtil.parsData(jsonMsg, InstallApkCommandRequest.class);
                if (commandRequest == null
                        || !commandRequest.getCommand().equals("BroadHW")) {
                    return;
                }

                //目前只有快速安装应用使用了Extra参数
                if (commandRequest.getExtra() != null) {
                    Log.i("BroadHW", "process batch install");
                    batchInstallPresenter.downloadAndInstall(commandRequest.getExtra());
                }
            }
        });

        //快速安装多个
        processList.add(new Process() {
            @Override
            void process(String jsonMsg) {
                InstallApksCommandRequest commandRequest =
                        (InstallApksCommandRequest) JsonUtil.parsData(jsonMsg, InstallApksCommandRequest.class);
                if (commandRequest == null
                        || !commandRequest.getCommand().equals("BroadHW")) {
                    return;
                }

                //目前只有快速安装应用使用了Extra参数
                if (commandRequest.getExtras() != null && !commandRequest.getExtras().isEmpty()) {
                    Log.i("BroadHW", "process batch install list");
                    batchInstallPresenter.downloadAndInstall(commandRequest.getExtras());
                }
            }
        });

        //用户手机的本地应用生成下载快捷方式
        processList.add(new Process() {
            @Override
            void process(String jsonMsg) {
                UserLocalInstalledAppInfoRequest commandRequest =
                        (UserLocalInstalledAppInfoRequest) JsonUtil.parsData(jsonMsg, UserLocalInstalledAppInfoRequest.class);

                if (commandRequest == null
                        || !commandRequest.getCommand().equals("BroadHW")) {
                    return;
                }

                if (commandRequest.getClassName().equals("UserLocalInstalledAppInfoRequest")
                        && commandRequest.getAppPackageList() != null) {
                    Log.i("BroadHW", "process user local installed app list");
                    prevShortcutPresenter.process(commandRequest.getAppPackageList(), commandRequest.isbRecommendMsg(), commandRequest.getUserInfo());
                }
            }
        });

        //市场图标隐藏
        processList.add(new Process() {
            @Override
            void process(String jsonMsg) {
                ChannelIconInfo commandRequest =
                        (ChannelIconInfo) JsonUtil.parsData(jsonMsg, ChannelIconInfo.class);
                if (commandRequest == null
                        || !commandRequest.getCommand().equals("BroadHW")) {
                    return;
                }

                //目前只有快速安装应用使用了Extra参数
                if (TextUtils.equals(commandRequest.hwAction, "channelIcon")) {
                    Log.i("BroadHW", "process channelIcon");
                    channelIcon(commandRequest);
                }
            }
        });

        //整理图标
        processList.add(new Process() {
            @Override
            void process(String jsonMsg) {
                ArrangeDesktopInfo commandRequest =
                        (ArrangeDesktopInfo) JsonUtil.parsData(jsonMsg, ArrangeDesktopInfo.class);
                if (commandRequest == null
                        || !commandRequest.getCommand().equals("BroadHW")) {
                    return;
                }

                if (TextUtils.equals(commandRequest.hwAction, new ArrangeDesktopInfo().hwAction)) {
                    LauncherModel model = LauncherAppState.getInstance().getModel();
                    model.arrangeDesktop(GsonUtil.toJson(commandRequest.arrangeBean));
                }
            }
        });
    }

    public void init(PrevShortcutContract.InitPresent p) {
        if (p == null) {
            Log.i("BroadHW", "init PrevShortcutContract.p null");
        }
        prevShortcutPresenter = p;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String jsonMsg = intent.getStringExtra(CommonConstants.EXTRA_KEY_MSG);
        Log.i("BroadHW", "msg:" + jsonMsg);

        for (Process process : processList) {
            process.process(jsonMsg);
        }
    }

    private void channelIcon(ChannelIconInfo iconInfo) {
        LauncherAppState state = LauncherAppState.getInstanceNoCreate();
        if (state == null) {
            return;
        }
        state.getModel().channelIcon(iconInfo);
    }
}
