package com.afterlogic.aurora.drive.presentation.modules.main.model.router;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.FilesRouter;

import java.io.File;
import java.util.List;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFileListRouter extends FilesRouter{

    int FILE_SELECT_CODE = 1;

    void openImagePreview(AuroraFile target, List<AuroraFile> dirContent);

    void openLink(AuroraFile target);

    boolean canOpenFile(AuroraFile file);

    void openFile(AuroraFile remote, File file);

    void openSendTo(AuroraFile source, File file);

    void openSendTo(List<File> files);

    void openUploadFileChooser();

    void openReplace(List<AuroraFile> files);
}
