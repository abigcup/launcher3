package com.android.hwyun.view;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.android.hwyun.data.CardSharedPreferencesManager;
import com.android.launcher3.Application.MainApplication;
import com.android.launcher3.R;

import java.util.List;

public class LauncherSettingAdapter extends BaseAdapter {

    private static final String TAG = "DynamicCardAdapter";
    private Context mContext;
    private List<CardInfo> mData;

    private CardSharedPreferencesManager cpm = new CardSharedPreferencesManager();

    AdapterICallback callback;
    public LauncherSettingAdapter(Context context, List<CardInfo> list,AdapterICallback callback) {
        this.mContext = context;
        this.mData = list;
        this.callback = callback;
    }

    public void setData(List<CardInfo> infos){
        this.mData = infos;
    }

    public int getCount() {
        List<CardInfo> list = this.mData;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public Object getItem(int i) {
        return this.mData.get(i);
    }

    public long getItemId(int i) {
        return (long) getItem(i).hashCode();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = LayoutInflater.from(this.mContext).inflate(R.layout.launcher_setting_item, viewGroup, false);
            viewHolder.image = (ImageView) view2.findViewById(R.id.item_image);
            viewHolder.desc = (TextView) view2.findViewById(R.id.item_description);
            viewHolder.title = (TextView) view2.findViewById(R.id.item_title);
            viewHolder.checkBox = (Switch) view2.findViewById(R.id.item_checkbox);
            viewHolder.divider = view2.findViewById(R.id.divider_line);
            //viewHolder.checkButton = view2.findViewById(R.id.item_checkimage);
            view2.setTag(viewHolder);
        } else {
            view2 = view;
            viewHolder = (ViewHolder) view.getTag();
        }

        CardInfo cardInfo = this.mData.get(i);
        if(cardInfo.type == 1){
            viewHolder.image.setImageResource(R.drawable.card_manager_calendar_logo);
            viewHolder.title.setText("日历");
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.checkButton.setVisibility(View.VISIBLE);

            if(cardInfo.isBlog == true){
                viewHolder.checkButton.setBackgroundResource(R.drawable.blog_del);
            }else{
                viewHolder.checkButton.setBackgroundResource(R.drawable.unblog_add);
            }

            viewHolder.checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cpm = new CardSharedPreferencesManager();
                    if(cpm.getCalendar(mContext)){
                        //更新数据 更改图标
                        cpm.setCalendar(false,mContext);
                        viewHolder.checkButton.setBackgroundResource(R.drawable.unblog_add);
                        callback.refresh();
                        MainApplication.fistScreenViewHandler.sendEmptyMessage(1);
                    }else{
                        //更新数据 更改图标
                        cpm.setCalendar(true,mContext);
                        viewHolder.checkButton.setBackgroundResource(R.drawable.blog_del);
                        callback.refresh();
                        MainApplication.fistScreenViewHandler.sendEmptyMessage(1);
                    }
                }
            });
            viewHolder.desc.setText("");

        }
        if(cardInfo.type == 2){
            viewHolder.image.setImageResource(R.drawable.card_manager_common_used_logo);
            viewHolder.title.setText("常用应用");
            viewHolder.desc.setText("显示常用应用");
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkButton.setVisibility(View.GONE);

            if(cpm.getUsageapp(mContext)) {
                viewHolder.checkBox.setChecked(true);
            }else{
                viewHolder.checkBox.setChecked(false);
            }

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cpm.getUsageapp(mContext)) {
                        viewHolder.checkBox.setChecked(false);
                        cpm.setUsageapp(false,mContext);
                        callback.refresh();
                        MainApplication.fistScreenViewHandler.sendEmptyMessage(1);
                    }else{
                        viewHolder.checkBox.setChecked(true);
                        cpm.setUsageapp(true,mContext);
                        callback.refresh();
                        MainApplication.fistScreenViewHandler.sendEmptyMessage(1);
                    }
                }
            });
        }

        return view2;
    }

    static class ViewHolder {
        Switch checkBox;
        TextView desc;
        View divider;
        ImageView image;
        TextView title;

        ImageButton checkButton;
        ViewHolder() {
        }
    }



}
