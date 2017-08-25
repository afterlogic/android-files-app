package com.afterlogic.aurora.drive.data.common.network.util;

import com.afterlogic.aurora.drive.data.model.ApiResponse;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 25.08.17.
 * mail: mail@sunnydaydev.me
 */

public class ApiUtil {

    // Need to be public for retrolambda
    public static  <T extends ApiResponse> Single<T> checkResponse(Single<T> upstream){
        return upstream.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(result);
            } else {
                return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
            }
        });
    }

    // Need to be public for retrolambda
    public static  <T, R extends ApiResponse<T>> Single<T> checkResponseAndGetData(Single<R> upstream){
        return upstream
                .compose(ApiUtil::checkResponse)
                .map(ApiResponse::getResult);
    }
}
