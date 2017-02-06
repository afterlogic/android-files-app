package com.afterlogic.aurora.drive.data.common.cache;

import io.reactivex.Observable;

/**
 * Created by sashka on 03.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface SharedObservableStore {

    boolean contains(String repositoryId, String taskId);

    <T> Observable<T> get(String repositoryId, String taskId);

    void remove(String repositoryId, String taskId);

    <T> Observable<T> put(String repositoryId, String taskId, Observable<T> observable);
}
