package com.afterlogic.aurora.drive.core.common.util;

import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;

/**
 * Created by aleksandrcikin on 10.05.17.
 */

public class Optional<T> {

    @Nullable
    private T value;

    public Optional(@Nullable T value) {
        this.value = value;
    }

    public Optional() {
    }

    public void set(@Nullable T value) {
        this.value = value;
    }

    public T setAndGet(@Nullable T value) {
        this.value = value;
        return value;
    }

    @Nullable
    public T get() {
        return value;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (value != null) {
            consumer.consume(value);
        }
    }

    public void clear() {
        set(null);
    }
}
