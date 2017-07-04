package com.afterlogic.aurora.drive.core.common.rx;


import com.afterlogic.aurora.drive.core.common.util.Optional;
import com.annimon.stream.Stream;

import io.reactivex.Completable;
import io.reactivex.MaybeTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;

/**
 * Created by aleksandrcikin on 10.05.17.
 */

public class RxUtil {

    public static <U> SingleTransformer<U, U> trackSingleDisposable(Optional<Disposable> disposable) {
        return upstream -> upstream
                .doOnSubscribe(disposable::set)
                .doFinally(disposable::clear);
    }

    public static <U> MaybeTransformer<U, U> trackMaybeDisposable(Optional<Disposable> disposable) {
        return upstream -> upstream
                .doOnSubscribe(disposable::set)
                .doFinally(disposable::clear);
    }

    public static <U> ObservableTransformer<U, U> trackObservableDisposable(Optional<Disposable> disposable) {
        return upstream -> upstream
                .doOnSubscribe(disposable::set)
                .doFinally(disposable::clear);
    }

    public static Completable ignoreErrors(Completable upstream, Class<? extends  Throwable> errors) {
        return upstream.onErrorComplete(error -> Stream.of(errors).anyMatch(it -> it == error.getClass()));
    }
}
