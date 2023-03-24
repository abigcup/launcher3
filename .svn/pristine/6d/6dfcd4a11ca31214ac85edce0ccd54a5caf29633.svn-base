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
import com.android.hwyun.installrecommend.bean.response.RecommendAppsResponse;
import com.android.hwyun.installrecommend.contract.AssociatedAppsContract;
import com.android.hwyun.installrecommend.event.ClickAssociatedAppEvent;
import com.android.hwyun.installrecommend.presenter.AssociateAppDialogPresenter;
import com.android.launcher3.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by xuwei on 2019/1/8.
 */
public class RecommendAppsDialog extends BaseDialog
        implements View.OnClickListener, DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener, AssociatedAppsContract.PopDialog.IView {

    private ImageView ivClose;
    private TextView tvRecommend;
    private RecyclerView rvApps;
    private Context mContex;

    private RecommendAppsResponse recommendApps;

    private RecommendGamesAdapter gamesAdapter;
    private AssociatedAppsContract.PopDialog.IPresenter presenter;
    /**
     * 单例
     */
    public static RecommendAppsDialog dialog;

    /**
     * 单例显示Dialog
     */
    public static RecommendAppsDialog showDialog(Context context, RecommendAppsResponse response) {
        if (response == null || Utils.getListSize(response.getAppsAssociateList()) == 0) {
            return null;
        }
        if (dialog == null) {
            dialog = new RecommendAppsDialog(context);
            dialog.setOnShowListener(dialog);
            dialog.setOnDismissListener(dialog);
        }
        if (!dialog.isShowing()) {
            dialog.recommendApps = response;
            dialog.show();
            dialog.setPos();
        }
        return dialog;
    }

    private RecommendAppsDialog(Context context) {
        super(context, R.style.NoTitleDialog);
        this.mContex = context;
        setCanceledOnTouchOutside(false);
        presenter = new AssociateAppDialogPresenter(this);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_recommend_games);
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
        tvRecommend.setText(recommendApps.getAssociatedDesc());
        gamesAdapter = new RecommendGamesAdapter(recommendApps.getAppsAssociateList());
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
        dialog.gamesAdapter.setNewData(recommendApps.getAppsAssociateList());
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
