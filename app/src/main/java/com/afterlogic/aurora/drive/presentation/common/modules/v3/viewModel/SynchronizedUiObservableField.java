package com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel;

import androidx.databinding.ObservableField;
import android.os.Handler;
import android.os.Looper;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class SynchronizedUiObservableField<T> extends ObservableField<T> {

    private final Object lock = new Object();

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public SynchronizedUiObservableField(T value) {
        super(value);
    }

    public SynchronizedUiObservableField() {
    }

    @Override
    public void set(T value) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            super.set(value);
        } else {
            synchronized (lock) {
                try {
                    uiHandler.post(() -> {
                        synchronized (lock) {
                            super.set(value);
                            lock.notify();
                        }
                    });
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
