package com.ddy.httplib;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;


/**
 * 切换线程帮助类
 * Created by win7 on 2016/12/13.
 */

public class RxSchedulersHelper {
    public static  FlowableTransformer<ResponseBody,String> io_main(){
        return new FlowableTransformer<ResponseBody, String>() {
            @Override
            public Publisher<String> apply(Flowable<ResponseBody> upstream) {
                return upstream.map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody value) throws Exception {
                        BufferedSource bufferedSource = Okio.buffer(value.source());
                        String tempStr = bufferedSource.readUtf8();
                        bufferedSource.close();
                        return tempStr;
                    }
                });
            }
        };
    }
}
