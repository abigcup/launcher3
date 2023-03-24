package com.android.hwyun.ddyobs.constans;

/**
 * Created by xuwei on 2020/2/19.
 */
public class ObsConstans {

    /***同步状态0未同步，1同步中，2同步完成*/
    public static final int STATE_NOSYNC = 0;
    public static final int STATE_SYNCING = 1;
    public static final int STATE_SYNCCOMPLETE = 2;

    /***obs操作类型1文件列表，2上传文件，3用户桶下载文件，4app桶下载文件*/
    public static final int OPTYPE_FILELIST = 1;
    public static final int OPTYPE_UPLOADFILE = 2;
    public static final int OPTYPE_DOWNFILE = 3;
    public static final int OPTYPE_DOWNFILE_APP = 4;
}
