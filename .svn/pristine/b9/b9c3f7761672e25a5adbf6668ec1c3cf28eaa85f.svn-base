package com.android.hwyun.common.util;

import com.android.hwyun.batchinstall.hwcloud.ObsCert;
import com.android.hwyun.ddyobs.bean.response.CreatCertResponse;

/**
 * Created by xuwei on 2020/2/21.
 */
public class ObsUtils {
    public static ObsCert FromCertResponse(CreatCertResponse certResponse) {
        ObsCert obsCert = new ObsCert();
        obsCert.ak = certResponse.Token.Access;
        obsCert.sk = certResponse.Token.Secret;
        obsCert.securityToken = certResponse.Token.Securitytoken;
        obsCert.endPoint = certResponse.EndPoint;
        obsCert.bucketName = certResponse.BucketName;
        return obsCert;
    }
}
