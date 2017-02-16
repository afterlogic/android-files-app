package com.afterlogic.aurora.drive.data.common.repository;

import com.afterlogic.aurora.drive.data.model.ApiResponse;
import com.afterlogic.aurora.drive.core.common.interfaces.Creator;
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

    protected static <T, R> Single<T> withNetRawMapper(Single<ApiResponse<R>> observable, Mapper<T, ApiResponse<R>> mapper){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(mapper.map(result));
            } else {
                return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
            }
        });
    }

    protected static <T, R> Single<T> withNetMapper(Single<ApiResponse<R>> observable, Mapper<T, R> mapper){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(mapper.map(result.getResult()));
            } else {
                return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
            }
        });
    }

    protected static <T, R extends ApiResponse<T>> Single<T> withNetMapper(Single<R> observable){
        return observable.flatMap(result -> {
            if (result.isSuccess()){
                return Single.just(result.getResult());
            } else {
                return Single.error(new ApiResponseError(result.getErrorCode(), result.getErrorMessage()));
            }
        });
    }

    protected <T, R> Single<T> withReloginNetMapper(Creator<Single<ApiResponse<R>>> creator, Mapper<T, R> mapper){
        return withNetMapper(creator.create(), mapper)
                .onErrorResumeNext(error -> {
                    if (error instanceof ApiResponseError && ((ApiResponseError) error).getErrorCode() == ApiResponseError.AUTH_FAILED){
                        return mAuthRepository.relogin()
                                .andThen(withNetMapper(creator.create(), mapper));
                    } else {
                        return Single.error(error);
                    }
                });
    }
}
