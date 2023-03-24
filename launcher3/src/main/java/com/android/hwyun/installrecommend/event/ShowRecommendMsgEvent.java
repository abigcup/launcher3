package com.android.hwyun.installrecommend.event;

import com.android.hwyun.installrecommend.bean.response.RecommendAppsResponse;

/**
 * Created by xuwei on 2019/1/19.
 */
public class ShowRecommendMsgEvent {
    private RecommendAppsResponse recommendAppsResponse;

    public ShowRecommendMsgEvent(RecommendAppsResponse recommendAppsResponse) {
        this.recommendAppsResponse = recommendAppsResponse;
    }

    public RecommendAppsResponse getRecommendAppsResponse() {
        return recommendAppsResponse;
    }

    public void setRecommendAppsResponse(RecommendAppsResponse recommendAppsResponse) {
        this.recommendAppsResponse = recommendAppsResponse;
    }
}
