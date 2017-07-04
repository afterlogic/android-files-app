package com.afterlogic.aurora.drive.presentation.common.binding.utils;

import android.databinding.BaseObservable;

/**
 * Created by aleksandrcikin on 31.05.17.
 * mail: mail@sunnydaydev.me
 */

public class ObservableCommand extends BaseObservable {

    private boolean handled = true;

    public void fire() {
        if (handled) {
            handled = false;
            notifyChange();
        }
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled() {
        handled = true;
    }

}
