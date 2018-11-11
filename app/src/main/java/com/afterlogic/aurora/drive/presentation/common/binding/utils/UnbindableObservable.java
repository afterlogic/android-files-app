package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sashka on 07.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UnbindableObservable<T extends Observable> {

    private T mField;
    private AtomicBoolean mBinded = new AtomicBoolean(false);

    private Observable.OnPropertyChangedCallback mCallback = new SimpleListener(this::onChanged);

    private Set<UnbindableObservableListener<T>> mListeners = new HashSet<>();
    private Set<OnUnbindListener<T>> mUnbindListeners = new HashSet<>();

    public static <T extends Observable> UnbindableObservable<T> bind(T field) {
        return create(field)
                .bind();
    }

    public static <T extends Observable> UnbindableObservable<T> bind(T field, UnbindableObservable.Bag bag, UnbindableObservableListener<T> listener) {
        UnbindableObservable<T> unbindableObservable = create(field)
                .addListener(listener)
                .addTo(bag)
                .bind();
        unbindableObservable.notifyChanged();
        return unbindableObservable;
    }

    public static <T> UnbindableObservable<ObservableField<T>> bindToValue(ObservableField<T> field, UnbindableObservable.Bag bag, UnbindableObservableListener<T> listener) {
        UnbindableObservable<ObservableField<T>> unbindableObservable = create(field)
                .addListener(f -> listener.onChange(f.get()))
                .addTo(bag)
                .bind();
        unbindableObservable.notifyChanged();
        return unbindableObservable;
    }

    public static UnbindableObservable<ObservableBoolean> bindToValue(ObservableBoolean field, UnbindableObservable.Bag bag, UnbindableObservableListener<Boolean> listener) {
        UnbindableObservable<ObservableBoolean> unbindableObservable = create(field)
                .addListener(f -> listener.onChange(f.get()))
                .addTo(bag)
                .bind();
        unbindableObservable.notifyChanged();
        return unbindableObservable;
    }

    public static UnbindableObservable<ObservableInt> bindToValue(ObservableInt field, UnbindableObservable.Bag bag, UnbindableObservableListener<Integer> listener) {
        UnbindableObservable<ObservableInt> unbindableObservable = create(field)
                .addListener(f -> listener.onChange(f.get()))
                .addTo(bag)
                .bind();
        unbindableObservable.notifyChanged();
        return unbindableObservable;
    }

    public static <T extends Observable> UnbindableObservable<T> create(T field) {
        return new UnbindableObservable<>(field);
    }

    public UnbindableObservable(T field) {
        mField = field;
    }

    public UnbindableObservable<T> bind() {
        if (mBinded.getAndSet(true)) return this;
        mField.addOnPropertyChangedCallback(mCallback);
        return this;
    }

    public UnbindableObservable<T> unbind() {
        if (!mBinded.getAndSet(false)) return this;
        mField.removeOnPropertyChangedCallback(mCallback);
        Stream.of(mUnbindListeners).forEach(listener -> listener.onUnbind(mField));
        return this;
    }

    public UnbindableObservable<T> addListener(@NonNull UnbindableObservableListener<T> listener){
        mListeners.add(listener);
        return this;
    }

    public UnbindableObservable<T> addOnUnbindListener(@NonNull OnUnbindListener<T> listener) {
        mUnbindListeners.add(listener);
        return this;
    }

    public UnbindableObservable<T> addTo(Bag bag) {
        bag.add(this);
        return this;
    }

    public UnbindableObservable<T> notifyChanged() {
        onChanged();
        return this;
    }

    private void onChanged() {
        Stream.of(mListeners).forEach(it -> it.onChange(mField));
    }

    public interface UnbindableObservableListener<T>{
        void onChange(T field);
    }

    public interface OnUnbindListener<T>{
        void onUnbind(T field);
    }

    public static class Bag {

        private Set<UnbindableObservable> mObservables = new HashSet<>();

        public void add(@NonNull UnbindableObservable observable) {
            mObservables.add(observable);
        }

        public void bind() {
            Stream.of(mObservables).forEach(UnbindableObservable::bind);
        }

        public void unbind() {
            Stream.of(mObservables).forEach(UnbindableObservable::unbind);
        }

        public void unbindAndClear() {
            unbind();
            mObservables.clear();
        }
    }
}
