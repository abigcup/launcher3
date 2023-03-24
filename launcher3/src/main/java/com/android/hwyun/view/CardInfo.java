package com.android.hwyun.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.android.hwyun.data.CardSharedPreferencesManager;

import java.util.ArrayList;

public class CardInfo {
    public int cardClass;
    public long endTime;
    public String iconPath = "";
    public long id;
    public boolean isBlog;
    public boolean isCanMoved = false;
    public boolean isCanRemoved = false;
    public boolean isNewCard;
    public boolean isShowFunction = false;
    public boolean isUserActive = false;
    public int position = -1;
    public int promotionOrder = -1;
    public int promotionType = -1;
    public String promotionUrl = "";
    public float score;
    public long startTime;
    public String title = "";
    public int type;  // 1 : 日历  2 : 常用应用

    public CardInfo() {
    }

    public CardInfo(CardInfo cardInfo) {
        this.id = cardInfo.id;
        this.type = cardInfo.type;
        this.title = cardInfo.title;
        this.position = cardInfo.position;
        this.isCanRemoved = cardInfo.isCanRemoved;
        this.isCanMoved = cardInfo.isCanMoved;
        this.isBlog = cardInfo.isBlog;
        this.isNewCard = cardInfo.isNewCard;
        this.cardClass = cardInfo.cardClass;
        this.iconPath = cardInfo.iconPath;
        this.promotionUrl = cardInfo.promotionUrl;
        this.promotionType = cardInfo.promotionType;
        this.startTime = cardInfo.startTime;
        this.endTime = cardInfo.endTime;
        this.score = cardInfo.score;
        this.isUserActive = cardInfo.isUserActive;
    }

    public String toString() {
        return "id = " + this.id + " & type = " + this.type + " & title = " + this.title + " & position = " + this.position + " & isCanRemoved = " + this.isCanRemoved + " & isCanMoved = " + this.isCanMoved + " & isShowFunction = " + this.isShowFunction + " & isBlog = " + this.isBlog + " & isNewCard = " + this.isNewCard + " & cardClass = " + this.cardClass + " & promotionType = " + this.promotionType + " & promotionUrl = " + this.promotionUrl + " & iconPath = " + this.iconPath + " & promotionOrder = " + this.promotionOrder + " & startTime = " + this.startTime + " & endTime = " + this.endTime + " & score = " + this.score;
    }


    public ArrayList<CardInfo> initData(Context context){
        CardInfo info = new CardInfo();
        info.id = 1;
        info.type = 1;
        info.isBlog = new CardSharedPreferencesManager().getCalendar(context);
        CardInfo info1 = new CardInfo();
        info1.id = 2;
        info1.type = 2;
        info.isBlog = new CardSharedPreferencesManager().getUsageapp(context);
        ArrayList<CardInfo> infos = new ArrayList<>();

        infos.add(info);
        infos.add(info1);
        return infos;
    }


}
