package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesModel;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesModel extends BaseFilesModel{

    void setLogin(String login);

    void setMultiChoiseMode(boolean multiChoise);

    boolean isInMultiChoise();

    void setSelectedCount(int count);

    void setSetSelectedHasFolder(boolean hasFolder);
}
