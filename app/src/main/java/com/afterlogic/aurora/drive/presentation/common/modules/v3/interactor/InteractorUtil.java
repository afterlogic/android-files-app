package com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor;

import com.afterlogic.aurora.drive.model.error.AuthError;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Single;

/**
 * Created by sunny on 04.09.17.
 * mail: mail@sunnydaydev.me
 */

public class InteractorUtil {

    public static <T> Single<T> retryIfNotAuthError(int times, Single<T> upcoming) {

        AtomicInteger counter = new AtomicInteger();

        return upcoming.retry(error ->
                !(error instanceof AuthError || counter.incrementAndGet() == times)
        );
    }
}
