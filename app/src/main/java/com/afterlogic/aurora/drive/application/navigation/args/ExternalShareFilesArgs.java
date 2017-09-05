package com.afterlogic.aurora.drive.application.navigation.args;

import java.io.File;
import java.util.List;

/**
 * Created by aleksandrcikin on 15.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ExternalShareFilesArgs {

    private final List<File> file;

    public ExternalShareFilesArgs(List<File> file) {
        this.file = file;
    }

    public List<File> getFile() {
        return file;
    }
}
