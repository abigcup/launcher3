package com.android.hwyun.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.hwyun.data.CardSharedPreferencesManager;
import com.android.hwyun.view.AdapterICallback;
import com.android.hwyun.view.CardInfo;
import com.android.hwyun.view.DynamicCardAdapter;
import com.android.launcher3.R;

import java.util.ArrayList;

public class CardManagerActivity extends Activity {
    private ListView mBlogListview;
    private ListView mUnBlogListview;
    private ListView mGeneralListview;
    private DynamicCardAdapter mUnBlogCardAdapter; //未订阅
    private DynamicCardAdapter mBlogCardAdapter;   //已订阅
    private DynamicCardAdapter mGeneralAdapter;    //常用

    ArrayList<CardInfo> blogInfos = new ArrayList<>();
    ArrayList<CardInfo> unblogInfos = new ArrayList<>();
    ArrayList<CardInfo> generalInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_manager);
        initData();
        initView();
    }

    public TextView mBlogTitle;
    public TextView mUnBlogTitle;

    public TextView mIntelligentShowCardTitle;
    public void initView(){
        mBlogTitle = findViewById(R.id.blog_title);
        mBlogTitle.setText("已订阅");
        mUnBlogTitle = findViewById(R.id.unblog_title);
        mUnBlogTitle.setText("点击添加更多");
        mIntelligentShowCardTitle = findViewById(R.id.intelligent_show_card_title);
        mIntelligentShowCardTitle.setText("智能显示");
        mGeneralAdapter = new DynamicCardAdapter(this, generalInfos, new AdapterICallback() {
            @Override
            public void refresh() {
                dataChange();
            }
        });
        mBlogCardAdapter = new DynamicCardAdapter(this, blogInfos, new AdapterICallback() {
            @Override
            public void refresh() {
                dataChange();
            }
        });
        mUnBlogCardAdapter = new DynamicCardAdapter(this, unblogInfos, new AdapterICallback() {
            @Override
            public void refresh() {
                dataChange();
            }
        });
        this.mBlogListview = (ListView) findViewById(R.id.blog_drag_list);
        mBlogListview.setAdapter((ListAdapter)mBlogCardAdapter);

        this.mUnBlogListview = (ListView) findViewById(R.id.unblog_card_listview);
        mUnBlogListview.setAdapter((ListAdapter)mUnBlogCardAdapter);

        this.mGeneralListview = (ListView) findViewById(R.id.intelligent_show_card_listview);
        mGeneralListview.setAdapter((ListAdapter)mGeneralAdapter);

    }

    public interface CallBack {
        public void processResponse();
    }



    public void initData(){
        //初始化已订阅
        CardSharedPreferencesManager cpm = new CardSharedPreferencesManager();
        blogInfos.clear();
        unblogInfos.clear();
        generalInfos.clear();
        if(cpm.getCalendar(this) == true){
            CardInfo info = new CardInfo();
            info.id = 1;
            info.type = 1;
            info.isBlog = new CardSharedPreferencesManager().getCalendar(this);
            blogInfos.add(info);
        }

        //初始化未订阅
        if(cpm.getCalendar(this) == false){
            CardInfo info = new CardInfo();
            info.id = 1;
            info.type = 1;
            info.isBlog = new CardSharedPreferencesManager().getCalendar(this);
            unblogInfos.add(info);
        }

        //初始化常用
        if(true){
            CardInfo info1 = new CardInfo();
            info1.id = 2;
            info1.type = 2;
            info1.isBlog = new CardSharedPreferencesManager().getUsageapp(this);
            generalInfos.add(info1);
        }
    }


    public void dataChange(){
        initData();
        mBlogCardAdapter.notifyDataSetChanged();
        mUnBlogCardAdapter.notifyDataSetChanged();
    }

}