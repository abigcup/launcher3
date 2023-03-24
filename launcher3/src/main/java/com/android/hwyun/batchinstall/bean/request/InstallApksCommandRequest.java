package com.android.hwyun.batchinstall.bean.request;

import com.android.hwyun.batchinstall.bean.ExtraBean;

import java.util.List;

/**
 * Created by xuwei on 2019/4/10.
 */
public class InstallApksCommandRequest extends BaseWebsoketRequest {

    /**
     * extras : [{"OrderID":111,"TaskID":"111","UCID":"111","OBKey":"111","BucketType": 0},]
     */
    private List<ExtraBean> extras;

    public List<ExtraBean> getExtras() {
        return extras;
    }

    public void setExtras(List<ExtraBean> extras) {
        this.extras = extras;
    }
}
