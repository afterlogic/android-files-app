package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.FileListArgs;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineArgs extends FileListArgs {

    public OfflineArgs(Bundle args) {
        super(args);
    }

    public OfflineArgs() {
    }

    public boolean isManual() {
        return args.getBoolean("manual", false);
    }

    public void setManual(boolean manual) {
        args.putBoolean("manual", manual);
    }
}
