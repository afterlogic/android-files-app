package com.afterlogic.aurora.drive._unrefactored.data.common.api;

import com.afterlogic.aurora.drive.model.error.ApiError;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public interface ApiCallback<T> {
    void onSucces(T result);
    void onError(ApiError error);
}
