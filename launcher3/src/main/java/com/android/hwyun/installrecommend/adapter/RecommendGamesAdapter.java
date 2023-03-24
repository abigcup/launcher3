package com.android.hwyun.installrecommend.adapter;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hwyun.batchinstall.bean.DownloadFileInfo;
import com.android.hwyun.batchinstall.hwcloud.DownloadManager;
import com.android.hwyun.batchinstall.hwcloud.OnDownloadListener;
import com.android.hwyun.common.constants.CommonConstants;
import com.android.hwyun.installrecommend.event.ClickAssociatedAppEvent;
import com.android.hwyun.prevshortcut.bean.ResponeAppsShortcut;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.R;
import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by xuwei on 2019/1/8.
 */
public class RecommendGamesAdapter extends BaseQuickAdapter<ResponeAppsShortcut, RecommendGamesAdapter.ViewHolder>
        implements OnDownloadListener {

    private Map<Long, ViewHolder> mapGameHold = new HashMap<>();
    private Set<Long> allFengwoShort = new HashSet<>();     //已生成的蜂窝专区快捷方式

    public RecommendGamesAdapter(List<ResponeAppsShortcut> apps) {
        super(R.layout.item_recommend_game, apps);
    }

    @Override
    protected void convert(ViewHolder helper, final ResponeAppsShortcut item) {
        if (helper.getChannelID() != 0) {
            mapGameHold.remove(helper.getChannelID());
        }
        helper.setChannelID(item.getChanneld());
        mapGameHold.put(helper.getChannelID(), helper);

        ImageView ivIcon = helper.getView(R.id.pop_desktop_list_item_image);
        TextView tvName = helper.getView(R.id.pop_desktop_text_name);
        TextView tvTip = helper.getView(R.id.pop_desktop_text_user);
        final TextView tvInstall = helper.getView(R.id.pop_desktop_btn);
        Glide.with(ivIcon)
                .load(item.getAppImgUrl())
                .into(ivIcon);
        tvName.setText(item.getAppName());
        if (item.getInstalledpercent() == 0) {
            item.setInstalledpercent(new Random().nextInt(10) + 90);
        }
        tvTip.setText(String.format(tvTip.getResources().getString(R.string.recommend_tip), item.getInstalledpercent()));
        if (item.getAppType() == CommonConstants.SHORTCUT_SOURCE_FENGWO) {
            if (DownloadManager.getImpl().isDownloading(item.getChanneld())) {
                tvInstall.setText(R.string.pop_installing);
            } else if (LauncherModel.isExistShortcut(item.getAppName())
                    || allFengwoShort.contains(item.getId())) {
                tvInstall.setText(R.string.pop_open);
            } else {
                tvInstall.setText(R.string.pop_desktop_install);
            }
        } else {
            if (DownloadManager.getImpl().isDownloading(item.getChanneld())) {
                tvInstall.setText(R.string.pop_installing);
            } else if (AppUtils.isAppInstalled(item.getPackageName())) {
                tvInstall.setText(R.string.pop_open);
            } else {
                tvInstall.setText(R.string.pop_desktop_install);
            }
        }
        tvInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources resources = tvInstall.getResources();
                if (tvInstall.getText().equals(resources.getString(R.string.pop_open))) {
                    EventBus.getDefault().post(new ClickAssociatedAppEvent(item, CommonConstants.APP_STATE_OPEN));
                } else if (tvInstall.getText().equals(resources.getString(R.string.pop_desktop_install))) {
                    EventBus.getDefault().post(new ClickAssociatedAppEvent(item, CommonConstants.APP_STATE_INSTALL));
                }
            }
        });
    }

    @Override
    public void progress(DownloadFileInfo downloadFileInfo) {
        ViewHolder viewHolder = mapGameHold.get(downloadFileInfo.getDownloadID());
        if (viewHolder != null) {
            TextView tvInstall = viewHolder.getView(R.id.pop_desktop_btn);
            tvInstall.setText(R.string.pop_installing);
        }
    }

    @Override
    public void downloadCompleted(DownloadFileInfo downloadFileInfo) {

    }

    @Override
    public void installCompleted(DownloadFileInfo downloadFileInfo) {
        ViewHolder viewHolder = mapGameHold.get(downloadFileInfo.getDownloadID());
        if (viewHolder != null) {
            TextView tvInstall = viewHolder.getView(R.id.pop_desktop_btn);
            tvInstall.setText(R.string.pop_open);
        }
    }

    @Override
    public void error(DownloadFileInfo downloadFileInfo, int errorCode, String errorMessage) {
        ViewHolder viewHolder = mapGameHold.get(downloadFileInfo.getDownloadID());
        if (viewHolder != null) {
            TextView tvInstall = viewHolder.getView(R.id.pop_desktop_btn);
            tvInstall.setText(R.string.pop_desktop_install);
        }
    }

    public void updateFengwoShortState(long TopicID) {
        allFengwoShort.add(TopicID);
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseViewHolder {
        private long channelID;

        public ViewHolder(View view) {
            super(view);
        }

        public long getChannelID() {
            return channelID;
        }

        public void setChannelID(long channelID) {
            this.channelID = channelID;
        }
    }
}
