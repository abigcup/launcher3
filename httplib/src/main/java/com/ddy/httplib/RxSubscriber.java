package com.ddy.httplib;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class RxSubscriber<T> implements Subscriber<T> {


    public abstract void _onNext(T t);

    public abstract void _onError(String msg);

    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable t) {
        _onError(t.getMessage());
    }

    @Override
    public void onComplete() {

    }
}