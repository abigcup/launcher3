package com.android.hwyun.installrecommend.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.hwyun.common.dialog.BaseDialog;
import com.android.hwyun.common.util.Utils;
import com.android.hwyun.installrecommend.bean.response.RecommendAppsResponse;
import com.android.hwyun.installrecommend.service.DialogService;
import com.android.launcher3.R;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.Glide;

/**
 * Created by xuwei on 2019/1/8.
 */
public class RecommendMsgDialog extends BaseDialog
        implements View.OnClickListener, DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener {

    private RelativeLayout rlContent;
    private ImageView ivNoticeImg;
    private TextView tvNoticeText;
    private ImageView ivClose;

    private Context mContex;

    private RecommendAppsResponse recommendApps;
    /**
     * 单例
     */
    public static RecommendMsgDialog dialog;

    /**
     * 单例显示Dialog
     */
    public static RecommendMsgDialog showDialog(Context context, RecommendAppsResponse response) {
        if (response == null || Utils.getListSize(response.getAppsAssociateList()) == 0) {
            return null;
        }
        if (dialog == null) {
            dialog = new RecommendMsgDialog(context);
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

    private RecommendMsgDialog(Context context) {
        super(context, R.style.NoTitleDialog);
        this.mContex = context;
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_recommend_msg);
        ivClose = findViewById(R.id.notice_close);
        rlContent = findViewById(R.id.notice_content);
        ivNoticeImg = findViewById(R.id.notice_img);
        tvNoticeText = findViewById(R.id.notice_text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {
        tvNoticeText.setText(recommendApps.getToastTitle());
        Glide.with(ivNoticeImg)
                .load(recommendApps.getToastImg())
                .into(ivNoticeImg);
    }

    @Override
    public void initListener() {
        ivClose.setOnClickListener(this);
        rlContent.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == ivClose.getId()) {
            dismiss();
        } else if (view.getId() == rlContent.getId()) {
            DialogService.showRecommendDialog(mContex, recommendApps);
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dialog = null;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        if (recommendApps.getShowSeconds() > 0) {
            tvNoticeText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isShowing()) {
                        dismiss();
                    }
                }
            }, recommendApps.getShowSeconds()*1000);
        }
    }

    //重载空不让他悬浮
    @Override
    public void init() {

    }

    private void setPos() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.dimAmount = 0;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y = SizeUtils.dp2px(150);
        dialogWindow.setAttributes(lp);
    }
}
