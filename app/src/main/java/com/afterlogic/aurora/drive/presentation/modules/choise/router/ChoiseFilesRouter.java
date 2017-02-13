package com.afterlogic.aurora.drive.presentation.modules.choise.router;

import java.io.File;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface ChoiseFilesRouter {

    void closeWithResult(File result);

    void closeCurrent();
}
