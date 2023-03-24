package com.android.hwyun.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.android.hwyun.common.base.IInitView;


public abstract class BaseDialog extends Dialog implements IFloat, IInitView {

    public BaseDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    public void init() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 24) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBeforView();
        initView();
        initListener();
        initData();

    }


    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void addFloat() {
        if (!isShowing()) {
            show();
        }
    }

    @Override
    public void removeFloat() {
        if (isShowing()) {
            dismiss();
        }
    }

    @Override
    public void initDataBeforView() {
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        getWindow().setGravity(Gravity.CENTER);
//        lp.width = ScreenUtil.getCurrentScreenWidth1(getContext());
//        getWindow().setAttributes(lp);
    }
}
