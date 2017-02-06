package com.afterlogic.aurora.drive.data.common.repository;

import com.afterlogic.aurora.drive._unrefactored.model.ApiResponse;
import com.afterlogic.aurora.drive.data.common.Observator;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;

import io.reactivex.Single;

/**
 * Created by sashka on 01.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuthorizedRepository extends Observator {

    private final AuthRepository mAuthRepository;

    public AuthorizedRepository(SharedObservableStore cache,
                                String repositoryId,
                                AuthRepository authRepository) {
        super(cache, repositoryId);
        mAuthRepository = authRepository;
    }

    protected   <T, R> Single<T> withNetRawMapper(Single<ApiResponse<R>> observable, Mapper<T, ApiResponse<R>> mapper){
        return withNetRawMapper(observable.retry(), mapper, true);
    }

    private   <T, R> Single<T> withNetRawMapper(Single<ApiResponse<R>> observable, Mapper<T, ApiResponse<R>> mapper, boolean relogin){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(mapper.map(result));
            } else {
                if (relogin && result.getErrorCode() == ApiResponseError.AUTH_FAILED){
                    return mAuthRepository.relogin()
                            .andThen(withNetRawMapper(observable.retry(), mapper, false));
                } else {
                    return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
                }
            }
        });
    }

    protected <T, R> Single<T> withNetMapper(Single<ApiResponse<R>> observable, Mapper<T, R> mapper){
        return withNetMapper(observable, mapper, true);
    }

    private   <T, R> Single<T> withNetMapper(Single<ApiResponse<R>> observable, Mapper<T, R> mapper, boolean relogin){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(mapper.map(result.getResult()));
            } else {
                if (relogin && result.getErrorCode() == ApiResponseError.AUTH_FAILED){
                    return mAuthRepository.relogin()
                            .andThen(withNetMapper(observable.retry(), mapper, false));
                } else {
                    return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
                }
            }
        });
    }

    protected <T, R extends ApiResponse<T>> Single<T> withNetMapper(Single<R> observable){
        return withNetMapper(observable.retry(), true);
    }

    private <T, R extends ApiResponse<T>> Single<T> withNetMapper(Single<R> observable, boolean relogin){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(result.getResult());
            } else {
                if (relogin && result.getErrorCode() == ApiResponseError.AUTH_FAILED){
                    return mAuthRepository.relogin()
                            .andThen(withNetMapper(observable.retry(), false));
                } else {
                    return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
                }
            }
        });
    }
}
