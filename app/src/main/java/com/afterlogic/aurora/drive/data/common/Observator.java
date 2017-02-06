package com.afterlogic.aurora.drive.data.common;

import android.os.Handler;
import android.os.Looper;

import com.afterlogic.aurora.drive.core.common.interfaces.Creator;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@SuppressWarnings("unused")
public class Observator {

    private final SharedObservableStore mCache;
    private final String mObservatorId;

    private Handler mHandler =  new Handler(Looper.getMainLooper());

    public Observator(SharedObservableStore cache, String observatorId) {
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

        Holder<Boolean> firstHandled = new Holder<>(false);

        return Observable.concat(
                local.onErrorResumeNext(error -> {
                    MyLog.majorException(this, error);
                    return Observable.empty();
                }).doOnNext(result -> firstHandled.set(true)),

                cloud.onErrorResumeNext(error -> {
                    if (firstHandled.get()){
                        MyLog.e(this, error);
                        return Observable.empty();
                    } else {
                        return Observable.error(error);
                    }
                })
        );
    }

    synchronized protected <T> Observable<T> sharedObservableBeforeTerminate(String id, Creator<Observable<T>> creator){
        return sharedObservableBeforeTerminate(id, 0, creator);
    }

    synchronized protected <T> Maybe<T> sharedMaybeBeforeTerminate(String id, Creator<Maybe<T>> creator){
        return sharedObservableBeforeTerminate(id, 0, () -> creator.create().toObservable())
                .singleElement();
    }

    synchronized protected <T> Single<T> sharedSingleBeforeTerminate(String id, Creator<Single<T>> creator){
        return sharedObservableBeforeTerminate(id, 0, () -> creator.create().toObservable())
                .firstOrError();
    }

    synchronized protected <T> Observable<T> sharedObservableBeforeTerminate(String id,
                                                                             long cacheTime,
                                                                             Creator<Observable<T>> creator){
        if (mCache.contains(mObservatorId, id)){
            return mCache.get(mObservatorId, id);
        } else {
            Observable<T> observable = creator.create()
                    .compose(source -> handleComplete(source, id, 0));
            return mCache.put(mObservatorId, id, observable);
        }
    }

    synchronized protected <T> Observable<T> cached(String id, Creator<Observable<T>> creator){
        if (mCache.contains(mObservatorId, id)){
            return  mCache.get(mObservatorId, id);
        } else {
            Observable<T> observable = creator.create()
                    .compose(source -> handleComplete(source, id, 0))
                    .cache();
            return mCache.put(mObservatorId, id, observable);
        }
    }

    private <T> Observable<T> handleComplete(Observable<T> observable, String id, long cacheTime){
        Holder<Boolean> success = new Holder<>(true);
        return observable
                .doOnError(error -> success.set(false))
                .doOnTerminate(() -> {
                    if (success.get() && cacheTime > 0){
                        mHandler.postDelayed(
                                () -> mCache.remove(mObservatorId, id), cacheTime
                        );
                    } else {
                        mCache.remove(mObservatorId, id);
                    }
                });
    }

    private <T> Observable<T> withoutResponse(Observable<T> observable){
        return observable.onErrorResumeNext(throwable -> {
            return Observable.empty();
        }).flatMap(result -> Observable.empty());
    }

    synchronized protected void clearCache(String id){
        mCache.remove(mObservatorId, id);
    }
}
