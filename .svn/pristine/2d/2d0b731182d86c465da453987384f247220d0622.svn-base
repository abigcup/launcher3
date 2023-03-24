package com.android.hwyun.preapp.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.common.dialog.BaseDialog;
import com.android.hwyun.common.util.CLog;
import com.android.hwyun.common.util.DownloadUtil;
import com.android.hwyun.preapp.bean.PresetAppInfo;
import com.android.hwyun.prevshortcut.presenter.PreUpdateManager;
import com.android.launcher3.R;
import com.android.launcher3.download.TasksManager;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

public
/**
 * Created by xuwei on 2021/7/20.
 */

class PreAppUpdateDialog extends BaseDialog implements View.OnClickListener {

    private View viewButton;
    private View viewProgress;
    private TextView tvUpdate;
    private TextView tvQuit;
    private TextView tvUse;
    private TextView tvUpdateText;
    private ProgressBar progressApp;

    private Context mContex;
    private PresetAppInfo.InstallAppsBean appsBean;
    private static PreAppUpdateDialog dialog;
    private boolean typeForce;
    private UpdateCallback callback;

    /**
     * 单例显示Dialog
     */
    public static PreAppUpdateDialog showDialog(Context context, PresetAppInfo.InstallAppsBean appsBean, UpdateCallback callback) {
        if (dialog == null) {
            dialog = new PreAppUpdateDialog(context);
        }
        if (appsBean.UpdateType == CommonConstants.TYPE_OPTIONAL) {
            PreUpdateManager.getInstance().setCheckUpdateInfo(appsBean.AppPackageName, appsBean.VersionCode);
        }
        dialog.appsBean = appsBean;
        dialog.callback = callback;
        dialog.show();
        return dialog;
    }

    public PreAppUpdateDialog(Context context) {
        super(context, R.style.NoTitleDialog);
        this.mContex = context;
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_pre_update);
        setCanceledOnTouchOutside(false);
        typeForce = (this.appsBean != null && this.appsBean.UpdateType == CommonConstants.TYPE_FORCE);
        setCancelable(!typeForce);

        viewButton = findViewById(R.id.view_button);
        viewProgress = findViewById(R.id.view_progress);
        tvUpdate = findViewById(R.id.pop_update_btn_update);
        tvQuit = findViewById(R.id.pop_update_btn_cancel);
        tvUse = findViewById(R.id.pop_update_btn_open);
        tvUpdateText = findViewById(R.id.pop_update_notice);
        progressApp = findViewById(R.id.progress_download);
        setUpdateText();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        tvUpdate.setOnClickListener(this);
        tvQuit.setOnClickListener(this);
        tvUse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == tvUpdate.getId()) {
            download();
        } else if (v.getId() == tvQuit.getId()) {
            dissmissDialog();
        } else if (v.getId() == tvUse.getId()) {
            if (callback != null) {
                callback.onStartApp();
            }
            dissmissDialog();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dialog = null;
    }

    public static void dissmissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private boolean download() {
        showProgress(true);
        progressApp.setProgress(0);
        String url = appsBean.AppUrl;
        String apkPath = TasksManager.getImpl().createPath(appsBean.AppPackageName+"_preup");
        FileUtils.deleteFile(apkPath);
        FileDownloadLargeFileListener taskDownloadListener = new FileDownloadLargeFileListener() {
            @Override
            protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

            }

            @Override
            protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                progressApp.setProgress((int) ((double) soFarBytes / totalBytes * 100));
            }

            @Override
            protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                showProgress(false);
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                final String apkPath = task.getPath();
                progressApp.setProgress(100);
                tvUpdateText.setText(R.string.pop_installing);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final File file = new File(apkPath);
                            String bInstall = DownloadUtil.installAppSilent(file, "-r", true);
                            CLog.d("apkInfo.txt", "bInstall = " + bInstall);
                            if (DownloadUtil.isInstallSuccess(bInstall)) {
                                file.delete();
                                tvUpdateText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showLong(appsBean.AppName + mContex.getString(R.string.update_success));
                                        if (callback != null) {
                                            callback.onStartApp();
                                        }
                                        dissmissDialog();
                                    }
                                });
                            } else {
                                tvUpdateText.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showProgress(false);
                                        tvUpdateText.setText(R.string.update_install_failed);
                                    }
                                });
                            }
                            file.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                showProgress(false);
                FileDownloader.getImpl().clear(task.getId(), task.getTargetFilePath());
                tvUpdateText.setText(R.string.update_version_failed);
            }

            @Override
            protected void warn(BaseDownloadTask task) {
            }
        };

        BaseDownloadTask task = FileDownloader.getImpl().create(url)
                .setPath(apkPath)
                .setCallbackProgressTimes(100)
                .setListener(taskDownloadListener);
        TasksManager.getImpl()
                .addTaskForView(task);
        task.start();
        return true;
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            setUpdateText();
            setCancelable(false);
            viewProgress.setVisibility(View.VISIBLE);
            viewButton.setVisibility(View.GONE);
        } else {
            setCancelable(!typeForce);
            viewProgress.setVisibility(View.GONE);
            viewButton.setVisibility(View.VISIBLE);
        }
    }

    private void setUpdateText() {
        if (appsBean.UpdateType == CommonConstants.TYPE_FORCE) {
            tvUpdateText.setText(String.format(mContex.getString(R.string.pop_update_version), appsBean.AppName));
            tvQuit.setVisibility(View.VISIBLE);
            tvUse.setVisibility(View.GONE);
        } else {
            tvUpdateText.setText(String.format(mContex.getString(R.string.pop_update_version_update), appsBean.AppName));
            tvQuit.setVisibility(View.GONE);
            tvUse.setVisibility(View.VISIBLE);
        }
    }

    public interface UpdateCallback {
        void onStartApp();
    }
}
