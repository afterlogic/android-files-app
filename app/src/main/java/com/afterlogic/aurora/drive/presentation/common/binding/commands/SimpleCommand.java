package com.afterlogic.aurora.drive.presentation.common.binding.commands;

import androidx.databinding.BaseObservable;

/**
 * Created by sunny on 05.09.17.
 * mail: mail@sunnydaydev.me
 */

public class SimpleCommand extends BaseObservable {

    private boolean fired;

    public void fire() {

        fired = true;
        notifyChange();

    }

    public boolean handle() {

        if (fired) {

            fired = false;
            return true;

        } else {

            return false;

        }

    }
}
