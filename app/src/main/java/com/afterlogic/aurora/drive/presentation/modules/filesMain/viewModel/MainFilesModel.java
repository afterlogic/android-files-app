package com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesModel {

    void setFileTypes(List<FileType> types);

    void setCurrentFolder(AuroraFile folder);

    void setLogin(String login);

    void setMultiChoiseMode(boolean multiChoise);

    boolean isInMultiChoise();

    void setSelectedCount(int count);

    void setSetSelectedHasFolder(boolean hasFolder);
}
