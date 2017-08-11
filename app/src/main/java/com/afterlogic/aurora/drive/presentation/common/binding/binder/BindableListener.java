package com.afterlogic.aurora.drive.presentation.common.binding.binder;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

class BindableListener<T> {

    private final Bindable<T> field;

    BindableListener(Bindable<T> field) {
        this.field = field;
    }


    Bindable<T> getField() {
        return field;
    }

    protected void set(T value) {
        field.set(value);
    }
}
