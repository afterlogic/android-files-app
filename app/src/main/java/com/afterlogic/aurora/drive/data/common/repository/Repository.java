package com.afterlogic.aurora.drive.data.common.repository;

import com.afterlogic.aurora.drive.data.common.Observator;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.model.ApiResponse;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;

import io.reactivex.Single;

/**
 * Created by sashka on 01.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class Repository extends Observator {

    public Repository(SharedObservableStore cache, String repositoryId) {
        super(cache, repositoryId);
    }

    // Need to be public for retrolambda
    public static  <R, T extends ApiResponse<R>> Single<T> withRawNetMapper(Single<T> observable){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(result);
            } else {
                return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
            }
        });
    }

    // Need to be public for retrolambda
    public static  <T, R extends ApiResponse<T>> Single<T> withNetMapper(Single<R> observable){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(result.getResult());
            } else {
                return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
            }
        });
    }
}
