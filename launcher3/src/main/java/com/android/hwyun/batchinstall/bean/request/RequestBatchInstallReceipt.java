package com.android.hwyun.batchinstall.bean.request;

import com.android.hwyun.common.bean.RequestBase;

/**
 * Created by xuwei on 2018/10/16.
 */
public class RequestBatchInstallReceipt extends RequestBase {

    /**
     * OrderID : 1
     * TaskID : sample string 2
     * InstallStatus : 0
     * InstallRemark : sample string 3
     */

    public long OrderID;
    public String TaskID;
    public int InstallStatus;
    public String InstallRemark;
}
