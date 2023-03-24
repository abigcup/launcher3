package com.android.hwyun.common.base;

public interface IInitView {

    /**
     * initDataBeforView()
     *
     * @param
     * @return void    返回类型
     * @throws
     * @Title: initDataBeforView
     * @Description: 页面初始化前获得数据
     */
     void initDataBeforView();

    /**
     * initView()
     *
     * @param
     * @return void    返回类型
     * @throws
     * @Title: initView
     * @Description: 初始化布局
     */
     void initView();

    /**
     * initData()
     *
     * @param
     * @return void    返回类型
     * @throws
     * @Title: initData
     * @Description: 初始化数据
     */
     void initData();

    /**
     * initListener
     *
     * @param
     * @return void
     * @throws
     * @Description: 初始化事件
     */
     void initListener();
}
