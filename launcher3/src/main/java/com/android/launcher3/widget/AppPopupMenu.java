package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DragSource;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutDropTarget;
import com.android.launcher3.UninstallDropTarget;
import com.blankj.utilcode.util.Utils;

/**
 * 长按桌面图标，显示弹出菜单
 * @author tomchen
 * @date 2020/11/4
 */
public class AppPopupMenu extends PopupWindow {

    private static AppPopupMenu window;
    private int width;
    private int height;
    private View container;
    private View desktop_true_uninstall;
    private View desktop_true_add;
    private View desktop_true_delete;
    private View anchor;
    private Launcher mLauncher;

    public AppPopupMenu(Context context) {
        super(context);
        initView(context);
        initListener();
    }

    private void initListener() {
        // 添加真机桌面
        desktop_true_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anchor != null) {
                    ShortcutDropTarget.startInstallShortcut(anchor.getTag(), null);
                }
                dismissPopMenu();
            }
        });

        // 卸载
        desktop_true_uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anchor != null && mLauncher != null) {
                    UninstallDropTarget.startUninstallActivity(mLauncher, anchor.getTag());
                }
                dismissPopMenu();
            }
        });

        // 删除
        desktop_true_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anchor != null && mLauncher != null) {
                    DeleteDropTarget.removeWorkspaceOrFolderItem(mLauncher, (ItemInfo) anchor.getTag(), anchor);
                }
                dismissPopMenu();
            }
        });
    }

    private void initView(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_system_menu, null, false);
        container = contentView.findViewById(R.id.container);
        desktop_true_uninstall = contentView.findViewById(R.id.desktop_true_uninstall);
        desktop_true_add = contentView.findViewById(R.id.desktop_true_add);
        desktop_true_delete = contentView.findViewById(R.id.desktop_true_delete);

        setContentView(contentView);
        setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        // 测量当前view的大小
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        contentView.measure(w, h);
        width = contentView.getMeasuredWidth();//获取测量宽度px
        height = contentView.getMeasuredHeight();//获取测量高度px
    }

    public static void showPopMenu(View anchor, Launcher mLauncher, DragSource source) {
        dismissPopMenu();
        Context context = anchor.getContext();
        Object info = anchor.getTag();
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ShortcutDropTarget.supportsDrop(mLauncher, info) || UninstallDropTarget.supportsDrop(mLauncher, info) || (source.supportsDeleteDropTarget() && DeleteDropTarget.supportsDrop(info))) {
            window = new AppPopupMenu(context);
            window.setAnchor(anchor, mLauncher, source);
            int yoff = 0;// 设置背景图片，控制小箭头的位置
            if (y > window.height || getScreenHeight() - y - anchor.getHeight() < window.height) {
                yoff = -anchor.getHeight() - window.height;
                if (getScreenWidth() - x - anchor.getWidth() < window.width / 2) {
                    window.container.setBackgroundResource(R.drawable.bg_true_desktop_right);// 右下
                } else {
                    window.container.setBackgroundResource(R.drawable.bg_true_desktop);// 左下
                }
            } else {
                yoff = 0;
                if (getScreenWidth() - x - anchor.getWidth() < window.width / 2) {
                    window.container.setBackgroundResource(R.drawable.bg_true_desktop_top_right);// 右上
                } else {
                    window.container.setBackgroundResource(R.drawable.bg_true_desktop_top);// 左上
                }
            }
            window.showAsDropDown(anchor, 0, yoff);
        }
    }

    private void setAnchor(View anchor, Launcher mLauncher, DragSource source) {
        this.mLauncher = mLauncher;
        this.anchor = anchor;

        Object info = anchor.getTag();
        // 添加真机桌面
        desktop_true_add.setVisibility(ShortcutDropTarget.supportsDrop(mLauncher, info) ? View.VISIBLE : View.GONE);
        // 是否支持卸载
        desktop_true_uninstall.setVisibility(UninstallDropTarget.supportsDrop(mLauncher, info) ? View.VISIBLE : View.GONE);
        // 是否显示删除
        desktop_true_delete.setVisibility(source.supportsDeleteDropTarget() && DeleteDropTarget.supportsDrop(info) ? View.VISIBLE : View.GONE);
    }

    public static void dismissPopMenu() {
        if (window != null && window.isShowing()) {
            window.dismiss();
            window = null;
        }
    }

    /**
     * Return the width of screen, in pixel.
     *
     * @return the width of screen, in pixel
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    /**
     * Return the height of screen, in pixel.
     *
     * @return the height of screen, in pixel
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) Utils.getApp().getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) return -1;
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }
}
