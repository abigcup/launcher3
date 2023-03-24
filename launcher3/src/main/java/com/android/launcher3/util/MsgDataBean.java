package com.android.launcher3.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * Created by Tony on 2017/10/24.
 */

public class MsgDataBean{
    private String content = "";

    public MsgDataBean(String content) {
        this.content = content;
    }

    public byte[] parse() {
        byte[] body = content.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}
