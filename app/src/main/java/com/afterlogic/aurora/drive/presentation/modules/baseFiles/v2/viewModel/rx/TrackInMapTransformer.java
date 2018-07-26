package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.rx;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.disposables.Disposable;

/**
 * Created by aleksandrcikin on 27.07.17.
 * mail: mail@sunnydaydev.me
 */
public class TrackInMapTransformer<K> implements CompletableTransformer {

    private final K key;
    private final Map<K, Disposable> map;

    public TrackInMapTransformer(K key, Map<K, Disposable> map) {
        this.key = key;
        this.map = map;
    }

    @Override
    public CompletableSource apply(Completable upstream) {

        AtomicReference<Disposable> disposableAtomicReference = new AtomicReference<>();

        return upstream
                .doOnSubscribe(disposable -> {
                    Disposable current = map.get(key);
                    if (current != null) {
                        current.dispose();
                    }
                    map.put(key, disposable);
                    disposableAtomicReference.set(disposable);
                })
                .doFinally(() -> {
                    Disposable current = map.get(key);
                    if (current != null && disposableAtomicReference.get() == current) {
                        map.remove(key);
                    }
                });
    }
}
