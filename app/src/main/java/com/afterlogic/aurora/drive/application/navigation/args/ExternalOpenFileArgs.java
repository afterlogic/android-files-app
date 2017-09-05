package com.afterlogic.aurora.drive.application.navigation.args;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.io.File;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ExternalOpenFileArgs {

    private final AuroraFile remote;
    private final File local;

    public ExternalOpenFileArgs(AuroraFile remote, File local) {
        this.remote = remote;
        this.local = local;
    }

    public AuroraFile getRemote() {
        return remote;
    }

    public File getLocal() {
        return local;
    }
}
