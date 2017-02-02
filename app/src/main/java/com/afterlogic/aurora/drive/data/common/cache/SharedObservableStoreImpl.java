package com.afterlogic.aurora.drive.data.common.cache;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by sashka on 03.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class SharedObservableStoreImpl implements SharedObservableStore {

    private Map<String, HashMap<String, Observable>> mObservableCache = new HashMap<>();

    @Inject SharedObservableStoreImpl() {
    }

    @Override
    synchronized public boolean contains(String repositoryId, String taskId) {
        boolean contains = getRepositoryCache(repositoryId).containsKey(taskId);
        MyLog.d(this, "Contains: " + repositoryId + ":" + taskId + " = " + contains);
        return contains;
    }

    @Override
    synchronized public <T> Observable<T> get(String repositoryId, String taskId) {
        MyLog.d(this, "Get: " + repositoryId + ":" + taskId);
        //noinspection unchecked
        return getRepositoryCache(repositoryId).get(taskId);
    }

    @Override
    synchronized public void remove(String repositoryId, String taskId) {
        MyLog.d(this, "Remove: " + repositoryId + ":" + taskId);
        getRepositoryCache(repositoryId).remove(taskId);
    }

    @Override
    synchronized public  <T> Observable<T> put(String repositoryId, String taskId, Observable<T> observable) {
        MyLog.d(this, "Put: " + repositoryId + ":" + taskId);
        Observable cached;
        if (observable instanceof io.reactivex.internal.operators.observable.ObservableCache){
            cached = observable;
        } else {
            cached = observable.share();
        }
        getRepositoryCache(repositoryId).put(taskId, cached);
        //noinspection unchecked
        return cached;
    }

    private HashMap<String, Observable> getRepositoryCache(String repositoryId){
        HashMap<String, Observable> cache = mObservableCache.get(repositoryId);
        if (cache == null){
            cache = new HashMap<>();
            mObservableCache.put(repositoryId, cache);
        }
        return cache;
    }
}
