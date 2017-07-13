package com.afterlogic.aurora.drive.core.common.rx;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

/**
 * Created by aleksandrcikin on 19.05.17.
 * mail: mail@sunnydaydev.me
 */

public class DisposableBag {


    private final Set<Disposable> disposables = new HashSet<>();

    public <T> Maybe<T> track(Maybe<T> maybe) {
        AtomicReference<Disposable> reference = new AtomicReference<>();
        return maybe.doOnSubscribe(disposable -> trackDisposable(reference, disposable))
                .doOnDispose(() -> disposables.remove(reference.get()));
    }

    public <T> Single<T> track(Single<T> single) {
        AtomicReference<Disposable> reference = new AtomicReference<>();
        return single.doOnSubscribe(disposable -> trackDisposable(reference, disposable))
                .doOnDispose(() -> disposables.remove(reference.get()));
    }

    public <T> Observable<T> track(Observable<T> observable) {
        AtomicReference<Disposable> reference = new AtomicReference<>();
        return observable.doOnSubscribe(disposable -> trackDisposable(reference, disposable))
                .doOnDispose(() -> disposables.remove(reference.get()));
    }

    synchronized public void dispose() {
        Stream.of(disposables).forEach(Disposable::dispose);
        disposables.clear();
    }

    private void trackDisposable(AtomicReference<Disposable> reference, Disposable disposable) {
        reference.set(disposable);
        disposables.add(disposable);
    }
}
