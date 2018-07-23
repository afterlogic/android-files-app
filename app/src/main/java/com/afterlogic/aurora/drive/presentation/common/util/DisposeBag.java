package com.afterlogic.aurora.drive.presentation.common.util;

import androidx.annotation.NonNull;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 07.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class DisposeBag {

    private Set<Disposable> mBag = new HashSet<>();

    public void add(@NonNull Disposable disposable) {
        mBag.add(disposable);
    }

    public void remove(@NonNull Disposable disposable) {
        mBag.remove(disposable);
    }

    public void dispose() {
        Stream.of(mBag).forEach(Disposable::dispose);
        mBag.clear();
    }

    public <T> Observable<T> add(Observable<T> observable) {
        AtomicReference<Disposable> disposable = new AtomicReference<>();
        return observable
                .doOnSubscribe(it -> {
                    disposable.set(it);
                    add(it);
                })
                .doFinally(() -> remove(disposable.get()) );
    }
}
