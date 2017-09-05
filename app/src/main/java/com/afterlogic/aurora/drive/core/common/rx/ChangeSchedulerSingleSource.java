package com.afterlogic.aurora.drive.core.common.rx;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;

/**
 * Created by aleksandrcikin on 02.05.17.
 */
public class ChangeSchedulerSingleSource<T> implements SingleSource<T> {

    private final Single<T> chain;

    public static <T> ChangeSchedulerSingleSource<T> create(Single<T> single, Scheduler scheduler) {
        return new ChangeSchedulerSingleSource<>(single, scheduler);
    }

    private ChangeSchedulerSingleSource(Single<T> chain, Scheduler scheduler) {
        this.chain = chain.subscribeOn(scheduler);
    }

    @Override
    public void subscribe(SingleObserver<? super T> observer) {
        observer.onSubscribe(chain.subscribe(observer::onSuccess, observer::onError));
    }
}
