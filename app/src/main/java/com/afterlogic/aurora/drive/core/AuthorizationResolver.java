package com.afterlogic.aurora.drive.core;

import android.content.Context;

import com.afterlogic.aurora.drive.application.ActivityTracker;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.CoreScope;
import com.afterlogic.aurora.drive.core.common.contextWrappers.Notificator;
import com.afterlogic.aurora.drive.core.common.util.AppUtil;
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
 *
 */
@CoreScope
public class AuthorizationResolver {

    private static final Object RETRY = new Object();

    private enum AuthEvent { FAILED, DONE, ACCOUNT_CHANGED }

    private final AppRouter router;
    private final ActivityTracker activityTracker;
    private final Context appContext;
    private final Notificator notificator;

    private final PublishSubject<AuthEvent> authPublisher = PublishSubject.create();

    @Inject
    AuthorizationResolver(AppRouter router,
                          ActivityTracker activityTracker,
                          Context appContext,
                          Notificator notificator) {

        this.router = router;
        this.activityTracker = activityTracker;
        this.appContext = appContext;
        this.notificator = notificator;
    }

    public <T> Single<T> checkAuth(Single<T> upstream) {
        return checkAuth(upstream, Schedulers.io());
    }

    public <T> Single<T> checkAuth(Single<T> upstream, Scheduler observeOn) {

        AtomicBoolean relogged = new AtomicBoolean(false);

        return upstream
                .retryWhen(errorsFlow ->
                        errorsFlow.flatMap(error ->
                                handleError(error, relogged, observeOn)
                        )
                );

    }

    private Flowable<Object> handleError(Throwable error,
                                         AtomicBoolean relogged,
                                         Scheduler observeOn) {

        if (isAuthError(error) && !relogged.getAndSet(true)) {

            if (canRelogin()) {

                return relogin(observeOn);

            } else {

                notificator.notifyAuthRequired();

                // TODO: Maybe another error?
                return Flowable.error(new AuthError());

            }

        }

        return Flowable.error(error);
    }

    private boolean canRelogin() {
        return AppUtil.isProcess(null, appContext) && activityTracker.hasStartedActivity();
    }

    private Flowable<Object> relogin(Scheduler observeOn) {

        return authPublisher.toFlowable(BackpressureStrategy.LATEST)
                .doOnSubscribe(subscription -> router.navigateTo(AppRouter.LOGIN, true))
                .flatMap(event -> {
                    switch (event) {

                        case DONE:
                            return Flowable.just(RETRY);

                        case ACCOUNT_CHANGED:

                            // TODO: Maybe another error?
                            return Flowable.error(new AuthError());

                        case FAILED:
                            return Flowable.error(new AuthError());

                        default:
                            throw new IllegalArgumentException("Unknown type.");
                    }
                })
                .observeOn(observeOn);

    }

    public void onAuthorized() {
        authPublisher.onNext(AuthEvent.DONE);
    }

    public void onAuthorizationFailed() {
        authPublisher.onNext(AuthEvent.FAILED);
    }

    public void onAccountChanged() {
        authPublisher.onNext(AuthEvent.ACCOUNT_CHANGED);
    }

    private boolean isAuthError(Throwable error) {
        if (!(error instanceof ApiResponseError)) return false;

        ApiResponseError apiError = (ApiResponseError) error;
        int errorCode = apiError.getErrorCode();

        return errorCode == ApiResponseError.AUTH_FAILED
                || errorCode == 108;
    }
}
