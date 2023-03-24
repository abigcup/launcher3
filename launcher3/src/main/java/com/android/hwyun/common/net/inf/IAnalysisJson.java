package com.android.hwyun.common.net.inf;

/**
 * 数据解析
 *
 * @author linbinghuang
 */
public interface IAnalysisJson<T> {

    T getData(String json);
}
