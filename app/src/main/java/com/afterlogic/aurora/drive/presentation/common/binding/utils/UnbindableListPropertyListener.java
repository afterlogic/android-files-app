package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sashka on 10.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class UnbindableListPropertyListener<T extends ObservableList> implements Unbindable{

    private final T mField;
    private final SimpleOnListChangedCallback<T> mChangedCallback = new SimpleOnListChangedCallback<>(list -> notifyChange());

    private AtomicBoolean mBinded = new AtomicBoolean(false);
    private Set<OnChangedCallback<T>> mListeners = new HashSet<>();
    private Set<Runnable> mOnUnbind = new HashSet<>();

    public static <T extends ObservableList> UnbindableListPropertyListener<T> bind(T field) {
        UnbindableListPropertyListener<T> unbindable = create(field);
        unbindable.bind();
        return unbindable;
    }

    public static <T extends ObservableList> UnbindableListPropertyListener<T> create(T field) {
        return new UnbindableListPropertyListener<>(field);
    }

    UnbindableListPropertyListener(@NonNull T field) {
        mField = field;
    }

    public void bind() {
        if (mBinded.getAndSet(true)) return;

        mField.addOnListChangedCallback(mChangedCallback);
    }

    public void unbind() {
        if (!mBinded.getAndSet(false)) return;

        mField.removeOnListChangedCallback(mChangedCallback);
        Stream.of(mOnUnbind).forEach(Runnable::run);
    }

    public UnbindableListPropertyListener<T> addOnUnibindAction(Runnable action) {
        mOnUnbind.add(action);
        return this;
    }

    public UnbindableListPropertyListener<T> addTo(UnbindableBag bag) {
        bag.add(this);
        return this;
    }

    public UnbindableListPropertyListener<T> addListener(@NonNull OnChangedCallback<T> callback) {
        mListeners.add(callback);
        return this;
    }

    public void notifyChange() {
        if (!mBinded.get()) return;

        Stream.of(mListeners).forEach(it -> it.onChange(mField));
    }

}
