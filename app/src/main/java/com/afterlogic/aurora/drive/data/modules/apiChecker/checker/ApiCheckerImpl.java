package com.afterlogic.aurora.drive.data.modules.apiChecker.checker;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.modules.apiChecker.ApiCheckRepository;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.consts.Const;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.HttpUrl;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiCheckerImpl implements ApiChecker {

    private final ApiCheckRepository mP7Checker;
    private final ApiCheckRepository mP8Checker;

    @SuppressWarnings("WeakerAccess")
    @Inject public ApiCheckerImpl(@P7 ApiCheckRepository p7Checker,
                                  @P8 ApiCheckRepository p8Checker) {
        mP7Checker = p7Checker;
        mP8Checker = p8Checker;
    }

    @Override
    public Single<Integer> getApiVersion(HttpUrl domain){

        Maybe<Integer> p7 = intOrEmpty(
                mP7Checker.checkDomain(domain),
                Const.ApiVersion.API_P7
        ).doOnError(error -> MyLog.e(this, error))
                .onErrorResumeNext(Maybe.empty());

        Maybe<Integer> p8 = intOrEmpty(
                mP8Checker.checkDomain(domain),
                Const.ApiVersion.API_P8
        ).doOnError(error -> MyLog.e(this, error))
                .onErrorResumeNext(Maybe.empty());

        return Observable.concat(p8.toObservable(), p7.toObservable()).first(Const.ApiVersion.API_NONE);
    }

    private Maybe<Integer> intOrEmpty(Single<Boolean> observable, int value){
        return observable.flatMapMaybe(result -> {
            if (result){
                return Maybe.just(value);
            } else {
                return Maybe.empty();
            }
        });
    }
}
