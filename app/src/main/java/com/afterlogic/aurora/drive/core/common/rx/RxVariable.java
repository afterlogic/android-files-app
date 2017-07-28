package com.afterlogic.aurora.drive.core.common.rx;

import android.support.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class RxVariable<T> {

    private T value = null;
    private PublishSubject<Value<T>> publishSubject = PublishSubject.create();

    public RxVariable() {
    }

    public RxVariable(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        publishSubject.onNext(new Value<>(value));
    }

    public Observable<Value<T>> asObservable() {
        return Observable.concat(
                Observable.just(new Value<>(value)),
                publishSubject
        );
    }

    public static class Value<V> {

        private final V value;

        public Value(V value) {
            this.value = value;
        }

        @Nullable
        public V get() {
            return value;
        }

        public boolean nonNull() {
            return value != null;
        }
    }
}
