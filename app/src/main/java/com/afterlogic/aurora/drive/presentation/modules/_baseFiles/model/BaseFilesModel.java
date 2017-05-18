package com.afterlogic.aurora.drive.presentation.modules._baseFiles.model;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFilesModel {

    void setFileTypes(List<FileType> types);

    void setCurrentFolder(AuroraFile folder);

    void setRefreshing(boolean refreshing);

    void setErrorState(boolean errorState);
}
