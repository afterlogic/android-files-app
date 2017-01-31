package com.afterlogic.aurora.drive.core.rx;

import android.os.Handler;
import android.os.Looper;

import com.afterlogic.aurora.drive.core.interfaces.Creator;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class Observator {

    private final ObservableCache mCache;
    private final String mObservatorId;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public Observator(ObservableCache cache, String observatorId) {
        mCache = cache;
        mObservatorId = observatorId;
    }

    protected  <T, F, S> Observable<T> fuse(Observable<F> local, Mapper<T,F> localMapper,
                                            Observable<S> cloud, Mapper<T,S> cloudMapper){
        return fuse(
                local.map(localMapper::map),
                cloud.map(cloudMapper::map)
        );
    }

    /**
     * Fuse two observables safely. First observable is a local request and it can be failed (error
     * will be ignored). Second observable is a network request started only after first will ended.
     * Not use {@link Observable#concat(Iterable)} because if second catch error immediately then
     * {@link ObservableEmitter#onNext(Object)} will not called.
     */
    protected <T> Observable<T> fuse(Observable<T> local, Observable<T> cloud){
        return Observable.create(subscriber -> local.subscribe(
                subscriber::onNext,
                error -> {
                    error.printStackTrace();
                    startNextFusedObservable(cloud, subscriber);
                },
                () -> startNextFusedObservable(cloud, subscriber)
        ));
    }

    private <T> void startNextFusedObservable(Observable<T> next, ObservableEmitter<? super T> subscriber){
        mHandler.post(() -> next.subscribeOn(Schedulers.io()).subscribe(
                subscriber::onNext,
                subscriber::onError,
                subscriber::onComplete
        ));
    }

    synchronized protected <T> Observable<T> sharedBeforeComplete(String id, Creator<Observable<T>> creator){
        if (mCache.contains(mObservatorId, id)){
            //noinspection unchecked
            return (Observable<T>) mCache.get(mObservatorId, id);
        } else {
            Observable<T> observable = creator.create()
                    .doOnTerminate(() -> mCache.remove(mObservatorId, id));
            //noinspection unchecked
            return (Observable<T>) mCache.put(mObservatorId, id, observable);
        }
    }

    synchronized protected <T> Observable<T> sharedBeforeCompleteWithTimeCache(String id,
                                                                               long cacheTime,
                                                                               Creator<Observable<T>> creator){
        if (mCache.contains(mObservatorId, id)){
            //noinspection unchecked
            return (Observable<T>) mCache.get(mObservatorId, id);
        } else {
            Observable<T> observable = creator.create()
                    .doOnComplete(() -> mHandler.postDelayed(
                            () -> mCache.remove(mObservatorId, id), cacheTime
                    ))
                    .doOnError((data) -> mCache.remove(mObservatorId, id));
            //noinspection unchecked
            return (Observable<T>) mCache.put(mObservatorId, id, observable);
        }
    }

    synchronized protected <T> Observable<T> cached(String id, Creator<Observable<T>> creator){
        if (mCache.contains(mObservatorId, id)){
            //noinspection unchecked
            return (Observable<T>) mCache.get(mObservatorId, id);
        } else {
            Observable<T> observable = creator.create()
                    .doOnError((data) -> mCache.remove(mObservatorId, id));
            //noinspection unchecked
            return (Observable<T>) mCache.put(mObservatorId, id, observable);
        }
    }

    synchronized protected void clearCache(String id){
        mCache.remove(mObservatorId, id);
    }
}
