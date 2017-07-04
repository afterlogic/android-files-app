package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import android.databinding.Observable;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by sashka on 10.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class UnbindablePropertyListener<T extends Observable> implements Unbindable{

    private final T mField;
    private final SimpleOnPropertyChangedCallback mChangedCallback = new SimpleOnPropertyChangedCallback(this::notifyChange);

    private AtomicBoolean mBinded = new AtomicBoolean(false);
    private Set<OnChangedCallback<T>> mListeners = new HashSet<>();
    private Set<Runnable> mOnUnbind = new HashSet<>();

    public static <T extends Observable> UnbindablePropertyListener<T> bind(T field) {
        UnbindablePropertyListener<T> unbindable = create(field);
        unbindable.bind();
        return unbindable;
    }

    public static <T extends Observable> UnbindablePropertyListener<T> create(T field) {
        return new UnbindablePropertyListener<>(field);
    }

    UnbindablePropertyListener(@NonNull T field) {
        mField = field;
    }

    public void bind() {
        if (mBinded.getAndSet(true)) return;

        mField.addOnPropertyChangedCallback(mChangedCallback);
    }

    public void unbind() {
        if (!mBinded.getAndSet(false)) return;

        mField.removeOnPropertyChangedCallback(mChangedCallback);
        Stream.of(mOnUnbind).forEach(Runnable::run);
    }

    public UnbindablePropertyListener<T> addOnUnibindAction(Runnable action) {
        mOnUnbind.add(action);
        return this;
    }

    public UnbindablePropertyListener<T> addTo(UnbindableBag bag) {
        bag.add(this);
        return this;
    }

    public UnbindablePropertyListener<T> addListener(@NonNull OnChangedCallback<T> callback) {
        mListeners.add(callback);
        return this;
    }

    public void notifyChange() {
        if (!mBinded.get()) return;

        Stream.of(mListeners).forEach(it -> it.onChange(mField));
    }

}
