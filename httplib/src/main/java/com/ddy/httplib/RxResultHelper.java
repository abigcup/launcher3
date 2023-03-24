package com.ddy.httplib;



import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

/**
 * Rx处理服务器返回
 * Created by YoKey.
 */
public class RxResultHelper {
    public static <T> FlowableTransformer<BaseHttpResult<T>,T> handleeResult(){
        return new FlowableTransformer<BaseHttpResult<T>, T>() {
            @Override
            public Publisher<T> apply(Flowable<BaseHttpResult<T>> upstream) {
                return upstream.flatMap(new Function<BaseHttpResult<T>, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(BaseHttpResult<T> baseHttpResult) throws Exception {
                        if (baseHttpResult == null){
                            return Flowable.error(new Exception("网络错误"));
                        }else {
                            return Flowable.error(new Exception());
                        }
//                        return Flowable.empty(); //会回调onComplete这个方法
                    }
                }).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
    /**
     * 直接返回基类出去
     * */
    public static <T> FlowableTransformer<BaseHttpResult<T>,BaseHttpResult> handleBaseHttpResult(){
        return new FlowableTransformer<BaseHttpResult<T>, BaseHttpResult>() {
            @Override
            public Publisher<BaseHttpResult> apply(Flowable<BaseHttpResult<T>> upstream) {
                return upstream.flatMap(new Function<BaseHttpResult<T>, Publisher<BaseHttpResult>>() {
                    @Override
                    public Publisher<BaseHttpResult> apply(BaseHttpResult<T> baseHttpResult) throws Exception {
                        if (baseHttpResult == null){
                            return Flowable.error(new Exception("网络错误"));
                        }else {
                            return createBaseDate(baseHttpResult);
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    private static <T> Flowable<T> createDate(final T t){
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                e.onNext(t);
            }
        }, BackpressureStrategy.BUFFER);
    }
    private static  Flowable<BaseHttpResult> createBaseDate(final BaseHttpResult t){
        return Flowable.create(new FlowableOnSubscribe<BaseHttpResult>() {
            @Override
            public void subscribe(FlowableEmitter<BaseHttpResult> e) throws Exception {
                e.onNext(t);
            }
        }, BackpressureStrategy.BUFFER);
    }

}