package com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel;

import android.databinding.ObservableField;
import android.os.Handler;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UiObservableField<T> extends ObservableField<T> {

    private final Handler uiHandler = new Handler();

    public UiObservableField(T value) {
        super(value);
    }

    public UiObservableField() {
    }

    @Override
    public void set(T value) {
        uiHandler.post(() -> super.set(value));
    }
}
