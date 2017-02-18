package com.afterlogic.aurora.drive.presentation.modules.fileView.router;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.io.File;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewRouter {

    void openFile(AuroraFile remote, File file);

    void openSendTo(AuroraFile source, File file);
}
