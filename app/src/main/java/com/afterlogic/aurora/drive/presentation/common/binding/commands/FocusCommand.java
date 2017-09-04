package com.afterlogic.aurora.drive.presentation.common.binding.commands;

import android.databinding.BaseObservable;
import android.support.annotation.NonNull;

/**
 * Created by sunny on 02.09.17.
 * mail: mail@sunnydaydev.me
 */

public class FocusCommand extends BaseObservable {

    private String focusTarget = null;

    public boolean isFor(@NonNull String target) {
        if (target.equals(focusTarget)) {
            focusTarget = null;
            return true;
        }

        return false;
    }

    public void focus(String target) {
        focusTarget = target;
        notifyChange();
    }
}
