package com.android.hwyun.prevshortcut.bean;

import com.android.hwyun.batchinstall.bean.request.BaseWebsoketRequest;
import com.android.hwyun.common.bean.XBYUserInfo;

import java.util.List;

/**
 * Created by lijingying on 2019/1/7.
 *  V1.5.2，同步用户手机应用列表需求相关数据
 *   它走的辅助协议跟InstallApkCommandRequest相同，但参数不同。
 */
public class UserLocalInstalledAppInfoRequest extends BaseWebsoketRequest {

    private String className;
    private List<String/*包名*/> appPackageList;
    private boolean bRecommendMsg; //是否推荐提示
    private XBYUserInfo userInfo;

    public UserLocalInstalledAppInfoRequest(){
        command = "BroadHW";
        className = "UserLocalInstalledAppInfoRequest";
    }

    public void init(List<String/*包名*/> list, boolean bRecommendMsg, XBYUserInfo userInfo){
        this.appPackageList = list;
        this.bRecommendMsg = bRecommendMsg;
        this.userInfo = userInfo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getAppPackageList() {
        return appPackageList;
    }

    public void setAppPackageList(List<String> appPackageList) {
        this.appPackageList = appPackageList;
    }

    public boolean isbRecommendMsg() {
        return bRecommendMsg;
    }

    public void setbRecommendMsg(boolean bRecommendMsg) {
        this.bRecommendMsg = bRecommendMsg;
    }

    public XBYUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(XBYUserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
