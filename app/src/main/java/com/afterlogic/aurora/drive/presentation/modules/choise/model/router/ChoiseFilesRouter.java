package com.afterlogic.aurora.drive.presentation.modules.choise.model.router;

import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.router.FilesRouter;

import java.io.File;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface ChoiseFilesRouter extends FilesRouter{

    void closeWithResult(File result);

    void closeCurrent();
}
