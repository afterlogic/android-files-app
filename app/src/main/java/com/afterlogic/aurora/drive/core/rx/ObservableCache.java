package com.afterlogic.aurora.drive.core.rx;

import io.reactivex.Observable;

/**
 * Created by sashka on 03.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface ObservableCache {

    boolean contains(String repositoryId, String taskId);

    Observable get(String repositoryId, String taskId);

    void remove(String repositoryId, String taskId);

    Observable put(String repositoryId, String taskId, Observable observable);
}
