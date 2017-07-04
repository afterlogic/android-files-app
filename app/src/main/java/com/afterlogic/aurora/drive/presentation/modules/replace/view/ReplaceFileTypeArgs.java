package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.os.Bundle;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeArgs {
    private static final String CLASS = ReplaceFileTypeArgs.class.getName();

    private static final String KEY_TYPE = CLASS + ".type";

    private final Bundle args;

    public ReplaceFileTypeArgs(Bundle args) {
        this.args = args;
    }

    public ReplaceFileTypeArgs() {
        this(new Bundle());
    }

    public void setType(String type) {
        args.putString(KEY_TYPE, type);
    }

    public String getType() {
        return args.getString(KEY_TYPE);
    }

    public Bundle getBundle() {
        return args;
    }
}
