package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.view;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.Args;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FileListArgs extends Args {

    private static final String KEY_TYPE = FileListArgs.class.getName() + ".type";

    public FileListArgs(Bundle args) {
        super(args);
    }

    public FileListArgs() {
        super(new Bundle());
    }

    public String getType() {
        return getBundle().getString(KEY_TYPE);
    }

    public void setType(String type) {
        args.putString(KEY_TYPE, type);
    }
}
