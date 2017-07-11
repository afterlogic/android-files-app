package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.os.Bundle;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.Args;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceArgs extends Args {

    private static final String KEY_COPY_MODE = ReplaceArgs.class.getName() + ".copyMode";
    private static final String KEY_FILES = ReplaceArgs.class.getName() + ".files";

    public ReplaceArgs(Bundle args) {
        super(args);
    }

    public ReplaceArgs() {
        super();
    }

    public boolean isCopyMode() {
        return getBundle().getBoolean(KEY_COPY_MODE, false);
    }

    public List<AuroraFile> getFiles() {
        return getBundle().getParcelableArrayList(KEY_FILES);
    }

    public void setFiles(List<AuroraFile> files) {
        args.putParcelableArrayList(KEY_FILES, new ArrayList<>(files));
    }

    public void setCopyMode(boolean copyMode) {
        args.putBoolean(KEY_COPY_MODE, copyMode);
    }
}
