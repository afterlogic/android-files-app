package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFilesListModel {

    void setFileList(List<AuroraFile> files);

    void setCurrentFolder(AuroraFile folder);

    void setRefreshing(boolean isRefreshing);

    void setThumbNail(AuroraFile file, Uri thumbUri);

    void changeFile(AuroraFile previous, AuroraFile newFile);

    void removeFile(AuroraFile file);

    void addFile(AuroraFile file);

    List<AuroraFile> getFiles();
}
