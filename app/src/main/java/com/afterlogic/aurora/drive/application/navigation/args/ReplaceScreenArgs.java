package com.afterlogic.aurora.drive.application.navigation.args;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandrcikin on 17.07.17.
 * mail: mail@sunnydaydev.me
 */
public class ReplaceScreenArgs {

    private List<AuroraFile> files;

    public ReplaceScreenArgs(List<AuroraFile> files) {
        this.files = files;
    }

    public ReplaceScreenArgs(AuroraFile file) {
        this.files = new ArrayList<>();
        files.add(file);
    }

    public List<AuroraFile> getFiles() {
        return files;
    }
}
