package com.afterlogic.aurora.drive.core.common.rx;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sashka on 21.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ObservableScheduler {

    private static final Scheduler IMMEDIATE = Schedulers.from(Runnable::run);

    private Scheduler mIoScheduler;
    private Scheduler mMainScheduler;

    public ObservableScheduler(Scheduler ioScheduler, Scheduler mainScheduler) {
        mIoScheduler = ioScheduler;
        mMainScheduler = mainScheduler;
    }

    @SuppressWarnings("WeakerAccess")
    public Scheduler getIoScheduler() {
        return mIoScheduler;
    }

    @SuppressWarnings("WeakerAccess")
    public Scheduler getMainScheduler() {
        return mMainScheduler;
    }

    public <R> Observable<R> defaultSchedulers(Observable<R> observable){
        return observable
                .subscribeOn(getIoScheduler())
                .observeOn(getMainScheduler());
    }

    public <R> Single<R> defaultSchedulers(Single<R> observable){
        return observable
                .subscribeOn(getIoScheduler())
                .observeOn(getMainScheduler());
    }

    public <R> Maybe<R> defaultSchedulers(Maybe<R> observable){
        return observable
                .subscribeOn(getIoScheduler())
                .observeOn(getMainScheduler());
    }

    public Completable defaultSchedulers(Completable observable){
        return observable
                .subscribeOn(getIoScheduler())
                .observeOn(getMainScheduler());
    }

    public <R> Observable<R> immediate(Observable<R> observable){
        return observable
                .subscribeOn(IMMEDIATE)
                .observeOn(IMMEDIATE);
    }

    public <R> Single<R> immediate(Single<R> observable){
        return observable
                .subscribeOn(IMMEDIATE)
                .observeOn(IMMEDIATE);
    }

    public <R> Maybe<R> immediate(Maybe<R> observable){
        return observable
                .subscribeOn(IMMEDIATE)
                .observeOn(IMMEDIATE);
    }

    public Completable immediate(Completable observable){
        return observable
                .subscribeOn(IMMEDIATE)
                .observeOn(IMMEDIATE);
    }
}
