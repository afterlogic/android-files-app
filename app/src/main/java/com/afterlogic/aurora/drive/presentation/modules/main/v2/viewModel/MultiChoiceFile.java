package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by aleksandrcikin on 18.07.17.
 * mail: mail@sunnydaydev.me
 */

class MultiChoiceFile {

    private AuroraFile file;
    private boolean offlineEnabled;

    MultiChoiceFile(AuroraFile file, boolean offlineEnabled) {
        this.file = file;
        this.offlineEnabled = offlineEnabled;
    }

    AuroraFile getFile() {
        return file;
    }

    boolean isOfflineEnabled() {
        return offlineEnabled;
    }
}
