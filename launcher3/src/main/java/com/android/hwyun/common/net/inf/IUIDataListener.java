package com.android.hwyun.common.net.inf;


/**
 * 网络数据绑定
 *
 * @author lbs
 */
public interface IUIDataListener {

    void uiDataSuccess(Object object);

    void uiDataError(Exception error);
}
