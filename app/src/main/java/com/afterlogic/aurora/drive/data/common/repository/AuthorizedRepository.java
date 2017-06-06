package com.afterlogic.aurora.drive.data.common.repository;

import com.afterlogic.aurora.drive.core.common.interfaces.Creator;
import com.afterlogic.aurora.drive.core.common.util.ErrorUtil;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.model.ApiResponse;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by sashka on 01.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuthorizedRepository extends Repository {

    private final AuthRepository mAuthRepository;

    public AuthorizedRepository(SharedObservableStore cache,
                                String repositoryId,
                                AuthRepository authRepository) {
        super(cache, repositoryId);
        mAuthRepository = authRepository;
    }

    protected <T, R> Single<T> withReloginNetMapper(Creator<Single<ApiResponse<R>>> creator, Mapper<T, R> mapper){
        return withNetMapper(creator.create(), mapper)
                .onErrorResumeNext(error -> {
                    int code = ErrorUtil.getErrorCode(error);
                    if (code == ApiResponseError.AUTH_FAILED || code == 108){
                        return mAuthRepository.relogin()
                                .andThen(Single.defer(() -> withNetMapper(creator.create(), mapper)));
                    } else {
                        return Single.error(error);
                    }
                });
    }

    public <R> Single<R> withRelogin(Single<R> request) {
        return request.retryWhen(errors -> errors.flatMap(error -> {
            int code = ErrorUtil.getErrorCode(error);
            if (code == ApiResponseError.AUTH_FAILED || code == 108){
                return mAuthRepository.relogin()
                        .andThen(Flowable.just(new Object()));
            } else {
                return Flowable.error(error);
            }
        }));
    }
}
