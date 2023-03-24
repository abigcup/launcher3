package com.android.hwyun.installrecommend.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hwyun.batchinstall.hwcloud.DownloadManager;
import com.android.hwyun.common.dialog.BaseDialog;
import com.android.hwyun.common.util.Utils;
import com.android.hwyun.installrecommend.adapter.RecommendGamesAdapter;
import com.android.hwyun.installrecommend.bean.response.AssociatedAppsResponse;
import com.android.hwyun.installrecommend.contract.AssociatedAppsContract;
import com.android.hwyun.installrecommend.event.ClickAssociatedAppEvent;
import com.android.hwyun.installrecommend.presenter.AssociateAppDialogPresenter;
import com.android.launcher3.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by xuwei on 2019/1/8.
 */
public class AssociatedAppsDialog extends BaseDialog
        implements View.OnClickListener, DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener, AssociatedAppsContract.PopDialog.IView {

    private ImageView ivClose;
    private TextView tvRecommend;
    private RecyclerView rvApps;
    private Context mContex;

    private AssociatedAppsResponse associatedApps;

    private RecommendGamesAdapter gamesAdapter;
    private AssociatedAppsContract.PopDialog.IPresenter presenter;
    /**
     * 单例
     */
    public static AssociatedAppsDialog dialog;

    /**
     * 单例显示Dialog
     */
    public static AssociatedAppsDialog showDialog(Context context, AssociatedAppsResponse response) {
        if (response == null || Utils.getListSize(response.getAppsShortcutList()) == 0) {
            return null;
        }
        if (dialog == null) {
            dialog = new AssociatedAppsDialog(context);
            dialog.setOnShowListener(dialog);
            dialog.setOnDismissListener(dialog);
        }
        if (!dialog.isShowing()) {
            dialog.associatedApps = response;
            dialog.show();
            dialog.setPos();
        }
        return dialog;
    }

    private AssociatedAppsDialog(Context context) {
        super(context, R.style.NoTitleDialog);
        this.mContex = context;
        setCanceledOnTouchOutside(false);
        presenter = new AssociateAppDialogPresenter(this);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_associated_games);
        ivClose = findViewById(R.id.image_close);
        tvRecommend = findViewById(R.id.text_recommend);
        rvApps = findViewById(R.id.recycler_games);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(associatedApps.getAssociatedDesc())) {
            tvRecommend.setText(String.format(tvRecommend.getResources().getString(R.string.recommend_text), associatedApps.getInstalledName()));
        } else {
            tvRecommend.setText(associatedApps.getAssociatedDesc());
        }
        gamesAdapter = new RecommendGamesAdapter(associatedApps.getAppsShortcutList());
        rvApps.setAdapter(gamesAdapter);
        rvApps.setLayoutManager(new LinearLayoutManager(mContex));
    }

    @Override
    public void initListener() {
        ivClose.setOnClickListener(this);
    }

    @Override
    public void updateFengwoShortState(long TopicID) {
        if (gamesAdapter != null) {
            gamesAdapter.updateFengwoShortState(TopicID);
        }
    }

    @Override
    public void dismissPopDialog() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == ivClose.getId()) {
            dismiss();
        }
    }

    @Subscribe
    public void onClickAssociatedEvent(ClickAssociatedAppEvent event) {
        presenter.clickItem(event.getResponeAppsShortcut(), event.getAppState());
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        EventBus.getDefault().unregister(this);
        DownloadManager.getImpl().removeDownloadListener(gamesAdapter);
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        EventBus.getDefault().register(this);
        DownloadManager.getImpl().addDownloadListener(gamesAdapter);
        dialog.gamesAdapter.setNewData(associatedApps.getAppsShortcutList());
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dialog = null;
    }

    private void setPos() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }
}
