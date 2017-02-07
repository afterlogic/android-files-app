package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

/**
 * Created by sashka on 21.04.16.
 * mail: sunnyday.development@gmail.com
 */
public interface FileActionCallback {
    void onOpenFolder(AuroraFile folder);

    void showActions(AuroraFile file);

    void onFileClicked(AuroraFile file, List<AuroraFile> allFiles);

    void createFolder(String path, String type, String folderName);
}
