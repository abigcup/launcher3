package com.android.hwyun.common.net;


import android.util.Log;

import com.android.hwyun.common.net.inf.IAnalysisJson;
import com.android.hwyun.common.net.inf.IUIDataListener;
import com.ddy.httplib.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import io.reactivex.disposables.Disposable;


/**
 * 网路请求
 *
 * @author linbinghuang
 */
public class ActivityHttpHelper<T> extends BaseOkHttpHelper {

    /**
     * Ui绑定
     */
    private IUIDataListener mDataListener;
    /**
     * 数据解析回调
     */
    private IAnalysisJson mAnalysisJson;
    private Disposable getDisposable;
    private Disposable postDisposable;
    private Gson gson;

    public ActivityHttpHelper(final TypeToken<T> typeToken) {
        this(null, typeToken);
    }
    public ActivityHttpHelper() {
    }
    public ActivityHttpHelper(IUIDataListener dataListener, final TypeToken<T> typeToken) {
        mDataListener = dataListener;
        if (mAnalysisJson == null) {
            gson = new Gson();
            mAnalysisJson = new IAnalysisJson() {
                @Override
                public T getData(String json) {
                    BaseDataResult dataResult = (BaseDataResult) JsonUtil.parsData(json, BaseDataResult.class);
                    if (dataResult == null || dataResult.Data == null) {
                        return gson.fromJson(json, typeToken.getType());
                    }

                    dataResult.setData();
                    T t = gson.fromJson(dataResult.getJson(), typeToken.getType());
                    return t;
                }
            };
        }
    }

    public ActivityHttpHelper(IUIDataListener dataListener, IAnalysisJson analysisJson) {
        mDataListener = dataListener;
        mAnalysisJson = analysisJson;
    }

    public void UpdateUIDataListener(IUIDataListener dataListener){
        mDataListener = dataListener;
    }

    @Override
    public void onResponse(Object response) {
        try {
            if (mDataListener != null) {
                mDataListener.uiDataSuccess(response);
            }
        }catch (Exception ex){
            Log.e("ActivityHttpHelper","onResponse "+ex.getMessage());
        }
    }

    @Override
    public void onErrorResponse(Exception e) {
        try {
            if (mDataListener != null) {
                mDataListener.uiDataError(e);
            }
        }catch (Exception ex){
            Log.e("ActivityHttpHelper","onErrorResponse "+ex.getMessage());
        }
    }

    /**
     * get请求
     *
     * @param url
     */
    public void sendGetRequest(String url, int timeOut) {
        getDisposable = super.sendGetRequest(url, mAnalysisJson, timeOut);
    }

    /**
     * post请求
     */
    public void sendPostRequest(String url, Map<String, String> map, int timeOut) {
        sendPostRequest("http://app.ddyun.com/", url, map, timeOut);
    }
    public void sendPostRequest(String baseurl, String url, Map<String, String> map, int timeOut) {
        postDisposable = super.sendPostRequest(baseurl, url, map, mAnalysisJson, timeOut);
    }

    public void uploadResourcePost(String url, String picPath, Map<String, String> map, int
            timeOut) {
        postDisposable = super.uploadResourcePost(url, picPath,map, mAnalysisJson, timeOut);
    }
    /**
     * 停止访问
     */
    public void stopRequest() {
        if (getDisposable != null && !getDisposable.isDisposed()) {
            getDisposable.dispose();
            getDisposable = null;
        }
        if (postDisposable != null && !postDisposable.isDisposed()) {
            postDisposable.dispose();
            postDisposable = null;
        }
        mDataListener = null;
        gson = null;
    }

}
