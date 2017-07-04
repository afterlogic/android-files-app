package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import android.databinding.Observable;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by sashka on 10.04.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UnbindableBag {

    private Set<Unbindable> mUnbindables = new HashSet<>();

    public void add(@NonNull Unbindable unbindable) {
        mUnbindables.add(unbindable);
    }

    public void bindAll() {
        Stream.of(mUnbindables).forEach(Unbindable::bind);
    }

    public void unbindAll() {
        Stream.of(mUnbindables).forEach(Unbindable::unbind);
    }

    public void unbindAllAndClear() {
        unbindAll();
        mUnbindables.clear();
    }

    public <T extends Observable> void bind(T field, OnChangedCallback<T> listener) {
        UnbindablePropertyListener.bind(field)
                .addListener(listener)
                .addTo(this)
                .notifyChange();
    }
}
