package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.Args;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceArgs extends Args {

    private static final String KEY_COPY_MODE = ReplaceArgs.class.getName() + ".copyMode";

    public ReplaceArgs(Bundle args) {
        super(args);
    }

    public ReplaceArgs() {
    }

    public void setCopyMode(boolean copyMode) {
        getBundle().putBoolean(KEY_COPY_MODE, copyMode);
    }

    public boolean isCopyMode() {
        return getBundle().getBoolean(KEY_COPY_MODE, false);
    }
}
