package com.android.hwyun.batchinstall.bean;

/**
 * Created by xuwei on 2019/4/10.
 */
public class ExtraBean {
    /**
     * OrderID : 111
     * TaskID : 111
     * UCID : 111
     * OBKey : 111
     * BucketType: 0
     */

    private int OrderID;
    private String TaskID;
    private String UCID;
    private String OBKey;
    private String fileId;//走http直接下载需要的参数（新版如果有这个参数直接走新版，旧版本先不删除）
    /**
     * {@link com.android.hwyun.common.constants.CommonConstants#BUCKET_TYPE_USERS}
     */
    private int BucketType;

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int OrderID) {
        this.OrderID = OrderID;
    }

    public String getTaskID() {
        return TaskID;
    }

    public void setTaskID(String TaskID) {
        this.TaskID = TaskID;
    }

    public String getUCID() {
        return UCID;
    }

    public void setUCID(String UCID) {
        this.UCID = UCID;
    }

    public String getOBKey() {
        return OBKey;
    }

    public void setOBKey(String OBKey) {
        this.OBKey = OBKey;
    }

    public int getBucketType() {
        return BucketType;
    }

    public void setBucketType(int bucketType) {
        BucketType = bucketType;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "ExtraBean{" +
                "OrderID=" + OrderID +
                ", TaskID='" + TaskID + '\'' +
                ", UCID='" + UCID + '\'' +
                ", OBKey='" + OBKey + '\'' +
                ", fileId='" + fileId + '\'' +
                ", BucketType=" + BucketType +
                '}';
    }
}
