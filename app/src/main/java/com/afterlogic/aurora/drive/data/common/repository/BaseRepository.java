package com.afterlogic.aurora.drive.data.common.repository;

import com.afterlogic.aurora.drive.core.rx.ObservableCache;
import com.afterlogic.aurora.drive.core.rx.Observator;
import com.afterlogic.aurora.drive.data.common.error.ObservableApiError;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.model.ApiResponse;

import io.reactivex.Single;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class BaseRepository extends Observator {

    public BaseRepository(ObservableCache cache, String observatorId) {
        super(cache, observatorId);
    }

    public static  <T, R> Single<T> withNetRawMapper(Single<ApiResponse<R>> observable, Mapper<T, ApiResponse<R>> mapper){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(mapper.map(result));
            } else {
                return Single.error(new ObservableApiError(result.getErrorMessage(), result.getErrorCode()));
            }
        });
    }

    public static  <T, R> Single<T> withNetMapper(Single<ApiResponse<R>> observable, Mapper<T, R> mapper){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(mapper.map(result.getResult()));
            } else {
                return Single.error(new ObservableApiError(result.getErrorMessage(), result.getErrorCode()));
            }
        });
    }

    public static  <T, R extends ApiResponse<T>> Single<T> withNetMapper(Single<R> observable){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(result.getResult());
            } else {
                return Single.error(new ObservableApiError(result.getErrorMessage(), result.getErrorCode()));
            }
        });
    }
}
