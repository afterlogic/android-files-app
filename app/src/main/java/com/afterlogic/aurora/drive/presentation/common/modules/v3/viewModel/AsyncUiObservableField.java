package com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel;

import android.databinding.ObservableField;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class AsyncUiObservableField<T> extends ObservableField<T> {

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public AsyncUiObservableField(T value) {
        super(value);
    }

    public AsyncUiObservableField() {
    }

    @Override
    public void set(T value) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            super.set(value);
        } else {
            uiHandler.post(() -> super.set(value));
        }
    }
}
