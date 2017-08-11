package com.afterlogic.aurora.drive.core.common.rx;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Completable;
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

    public Completable track(Completable upsteam) {
        AtomicReference<Disposable> reference = new AtomicReference<>();
        return upsteam.doOnSubscribe(disposable -> trackDisposable(reference, disposable))
                .doFinally(() -> disposables.remove(reference.get()));
    }

    public <T> Maybe<T> track(Maybe<T> upsteam) {
        AtomicReference<Disposable> reference = new AtomicReference<>();
        return upsteam.doOnSubscribe(disposable -> trackDisposable(reference, disposable))
                .doFinally(() -> disposables.remove(reference.get()));
    }

    public <T> Single<T> track(Single<T> upsteam) {
        AtomicReference<Disposable> reference = new AtomicReference<>();
        return upsteam.doOnSubscribe(disposable -> trackDisposable(reference, disposable))
                .doFinally(() -> disposables.remove(reference.get()));
    }

    public <T> Observable<T> track(Observable<T> upsteam) {
        AtomicReference<Disposable> reference = new AtomicReference<>();
        return upsteam.doOnSubscribe(disposable -> trackDisposable(reference, disposable))
                .doFinally(() -> disposables.remove(reference.get()));
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
