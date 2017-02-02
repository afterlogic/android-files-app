package com.afterlogic.aurora.drive.data.common.service;

import com.afterlogic.aurora.drive.core.common.interfaces.Creator;

import java.util.Collection;
import java.util.Collections;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Action;

/**
 * Created by sashka on 10.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class LocalService {

    protected <D> Maybe<Collection<D>> resultOrEmptyMaybe(Creator<Collection<D>> collectionCreator){
        return Maybe.defer(() -> {
            Collection<D> collection = collectionCreator.create();
            if (collection == null || collection.isEmpty()){
                return Maybe.empty();
            } else {
                return Maybe.just(collection);
            }
        });
    }

    protected <D> Single<Collection<D>> resultOrEmptyCollection(Creator<Collection<D>> collectionCreator){
        return Single.defer(() -> {
            Collection<D> collection = collectionCreator.create();
            if (collection == null){
                collection = Collections.emptyList();
            }
            return Single.just(collection);
        });
    }

    protected  <C> Maybe<C> valueOrEmpty(Creator<C> valueCreator){
        return Maybe.defer(() -> {
            C value = valueCreator.create();
            if (value == null){
                return Maybe.empty();
            } else {
                return Maybe.just(value);
            }
        });
    }

    protected Completable defer(Action runnable){
        return Completable.fromAction(runnable);
    }
}
