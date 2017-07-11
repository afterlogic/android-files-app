package com.afterlogic.aurora.drive.presentation.common.modules.v3.view;

import android.os.Bundle;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class Args {

    protected final Bundle args;

    public Args(Bundle args) {
        this.args = args;
    }

    public Args() {
        this(new Bundle());
    }

    public Bundle getBundle() {
        return args;
    }
}
