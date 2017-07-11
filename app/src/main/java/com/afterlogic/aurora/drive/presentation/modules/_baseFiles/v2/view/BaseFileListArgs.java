package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.Args;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class BaseFileListArgs extends Args {

    private static final String KEY_TYPE = BaseFileListArgs.class.getName() + ".type";

    public BaseFileListArgs(Bundle args) {
        super(args);
    }

    public BaseFileListArgs() {
        super(new Bundle());
    }

    public String getType() {
        return getBundle().getString(KEY_TYPE);
    }

    public void setType(String type) {
        args.putString(KEY_TYPE, type);
    }
}
