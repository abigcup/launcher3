package com.android.hwyun.statistics;

import com.android.hwyun.common.bean.RequestBase;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;

/**
 * Created by lijingying on 2019/3/19.
 */
public class StatisticsRequestInfo extends RequestBase {

    /**
     * PointCode : 1
     * DeviceVendor : sample string 2
     * DeviceModel : sample string 3
     * SystemVersion : sample string 4
     * DeviceRAM : sample string 5
     * DeviceResolution : sample string 6
     * AppChannel : sample string 10
     * DeviceIP : sample string 11
     * OrderId : 1
     * PointArg : sample string 13
     */

    private int PointCode;
    private String DeviceVendor;
    private String DeviceModel;
    private String SystemVersion;
    private String DeviceResolution;
    private String DeviceIP;
    private int OrderId;
    private String PointArg;

    /**
     *  自动初始化相关基础信息
     */
    public void init(){
        DeviceVendor = DeviceUtils.getManufacturer();
        DeviceModel = DeviceUtils.getModel();
        SystemVersion = DeviceUtils.getSDKVersionName();
        DeviceResolution = ""+ ScreenUtils.getScreenWidth()+"x"+ ScreenUtils.getScreenHeight();
        DeviceIP = IPUtil.getNetIp();
    }

    public int getPointCode() {
        return PointCode;
    }

    public void setPointCode(int PointCode) {
        this.PointCode = PointCode;
    }

    public String getDeviceVendor() {
        return DeviceVendor;
    }

    public void setDeviceVendor(String DeviceVendor) {
        this.DeviceVendor = DeviceVendor;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String DeviceModel) {
        this.DeviceModel = DeviceModel;
    }

    public String getSystemVersion() {
        return SystemVersion;
    }

    public void setSystemVersion(String SystemVersion) {
        this.SystemVersion = SystemVersion;
    }

    public String getDeviceResolution() {
        return DeviceResolution;
    }

    public void setDeviceResolution(String DeviceResolution) {
        this.DeviceResolution = DeviceResolution;
    }

    public String getDeviceIP() {
        return DeviceIP;
    }

    public void setDeviceIP(String DeviceIP) {
        this.DeviceIP = DeviceIP;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int OrderId) {
        this.OrderId = OrderId;
    }

    public String getPointArg() {
        return PointArg;
    }

    public void setPointArg(String PointArg) {
        this.PointArg = PointArg;
    }
}
