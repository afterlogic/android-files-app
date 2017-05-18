package com.afterlogic.aurora.drive.presentation.modules.offline.model.router;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.io.File;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface OfflineRouter {
    void openFile(AuroraFile remote, File file);
    void openSendTo(AuroraFile source, File file);
    void goToOnline();
    void openAuth();

    boolean canOpen(AuroraFile file);
}
