package com.afterlogic.aurora.drive.core.rx;

import com.afterlogic.aurora.drive.core.MyLog;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by sashka on 03.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ObservableCacheImpl implements ObservableCache {

    private HashMap<String, HashMap<String, Observable>> mObservableCache = new HashMap<>();

    @Inject public ObservableCacheImpl() {
    }

    @Override
    synchronized public boolean contains(String repositoryId, String taskId) {
        boolean contains = getRepositoryCache(repositoryId).containsKey(taskId);
        MyLog.d(this, "Contains: " + repositoryId + ":" + taskId + " = " + contains);
        return contains;
    }

    @Override
    synchronized public Observable get(String repositoryId, String taskId) {
        MyLog.d(this, "Get: " + repositoryId + ":" + taskId);
        return getRepositoryCache(repositoryId).get(taskId);
    }

    @Override
    synchronized public void remove(String repositoryId, String taskId) {
        MyLog.d(this, "Remove: " + repositoryId + ":" + taskId);
        getRepositoryCache(repositoryId).remove(taskId);
    }

    @Override
    synchronized public Observable put(String repositoryId, String taskId, Observable observable) {
        MyLog.d(this, "Put: " + repositoryId + ":" + taskId);
        Observable cached = observable.cache();
        getRepositoryCache(repositoryId).put(taskId, cached);
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
