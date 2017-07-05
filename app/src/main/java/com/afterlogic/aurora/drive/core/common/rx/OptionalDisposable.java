package com.afterlogic.aurora.drive.core.common.rx;

import com.afterlogic.aurora.drive.core.common.util.Optional;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

/**
 * Created by aleksandrcikin on 08.06.17.
 * mail: mail@sunnydaydev.me
 */

public class OptionalDisposable extends Optional<Disposable> {

    public Completable track(Completable upstream) {
        return upstream.doOnSubscribe(this::set)
                .doFinally(() -> this.set(null));
    }

    public <T> Maybe<T> track(Maybe<T> upstream) {
        return upstream.doOnSubscribe(this::set)
                .doFinally(() -> this.set(null));
    }

    public <T> Single<T> track(Single<T> upstream) {
        return upstream.doOnSubscribe(this::set)
                .doFinally(() -> this.set(null));
    }

    public <T> Observable<T> track(Observable<T> upstream) {
        return upstream.doOnSubscribe(this::set)
                .doFinally(() -> this.set(null));
    }

    public void disposeAndClear() {
        synchronized (this) {
            ifPresent(Disposable::dispose);
            set(null);
        }
    }
}
