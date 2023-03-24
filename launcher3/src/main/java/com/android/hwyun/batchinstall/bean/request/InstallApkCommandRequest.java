package com.android.hwyun.batchinstall.bean.request;

import com.android.hwyun.batchinstall.bean.ExtraBean;

/**
 * Created by xuwei on 2018/10/18.
 */
public class InstallApkCommandRequest extends BaseWebsoketRequest {

    public InstallApkCommandRequest() {
    }

    /**
     * extra : {"OrderID":111,"TaskID":"111","UCID":"111","OBKey":"111","BucketType": 0}
     */

    private ExtraBean extra;

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }
}
