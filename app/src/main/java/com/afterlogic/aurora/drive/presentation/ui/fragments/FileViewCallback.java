package com.afterlogic.aurora.drive.presentation.ui.fragments;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 30.03.16.
 * mail: sunnyday.development@gmail.com
 */
public interface FileViewCallback {
    void updateTitle(String title);
    boolean requireFullscreen(boolean fullscreen);
    void downloadFile(AuroraFile file);

    void renameFile(AuroraFile file);
    void deleteFile(AuroraFile file);
    void makeFileOffline(AuroraFile file, boolean offline);
    void sendFile(AuroraFile fileP7);
}
