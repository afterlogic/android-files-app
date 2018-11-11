package com.afterlogic.aurora.drive.presentation.modules.baseFiles.model;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Storage;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFilesModel {

    void setFileTypes(List<Storage> types);

    void setCurrentFolder(AuroraFile folder);

    void setRefreshing(boolean refreshing);

    void setErrorState(boolean errorState);
}
