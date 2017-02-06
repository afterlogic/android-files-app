package com.afterlogic.aurora.drive._unrefactored.data.common.api;

import retrofit2.Call;

/**
 * Created by sashka on 26.05.16.
 * mail: sunnyday.development@gmail.com
 */
public interface CallCreator<T, R> {
    Call<R> createCall(T apiInterface);
}
