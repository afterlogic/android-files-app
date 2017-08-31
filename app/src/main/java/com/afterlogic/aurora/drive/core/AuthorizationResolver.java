package com.afterlogic.aurora.drive.core;

import com.afterlogic.aurora.drive.application.ActivityTracker;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.CoreScope;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive.model.error.AuthError;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by sunny on 29.08.17.
 */
@CoreScope
public class AuthorizationResolver {

    private final AppRouter router;
    private final ActivityTracker activityTracker;

    private final PublishSubject<Boolean> authPublisher = PublishSubject.create();

    @Inject
    AuthorizationResolver(AppRouter router, ActivityTracker activityTracker) {
        this.router = router;
        this.activityTracker = activityTracker;
    }

    public <T> Single<T> checkAuth(Single<T> upstream) {
        return checkAuth(upstream, Schedulers.io());
    }

    public <T> Single<T> checkAuth(Single<T> upstream, Scheduler observeOn) {

        AtomicBoolean reloginHandled = new AtomicBoolean(false);

        return upstream.retryWhen(errorsFlow -> errorsFlow.flatMap(error -> {

            if (activityTracker.hasStartedActivity()
                    && !reloginHandled.getAndSet(true)
                    && isAuthError(error)) {

                return authPublisher.toFlowable(BackpressureStrategy.LATEST)
                        .doOnSubscribe(disposable -> router.navigateTo(AppRouter.LOGIN, true))
                        .flatMap(authorized -> {
                            if (authorized) {
                                return Flowable.just(true);
                            } else {
                                return Flowable.error(new AuthError());
                            }
                        })
                        .observeOn(observeOn);

            }

            return Flowable.<Boolean>error(error);
        }));

    }

    public void onAuthorized() {
        authPublisher.onNext(true);
    }

    public void onAuthorizationFailed() {
        authPublisher.onNext(false);
    }

    private boolean isAuthError(Throwable error) {
        if (!(error instanceof ApiResponseError)) return false;

        ApiResponseError apiError = (ApiResponseError) error;
        int errorCode = apiError.getErrorCode();

        return errorCode == ApiResponseError.AUTH_FAILED
                || errorCode == 108;
    }
}
