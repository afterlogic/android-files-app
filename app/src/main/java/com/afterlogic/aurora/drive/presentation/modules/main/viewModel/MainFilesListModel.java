package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListModel;

import java.util.List;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesListModel extends BaseFilesListModel{

    void setMultiChoiseMode(boolean mode);

    boolean isMultiChoise();

    List<AuroraFile> getMultiChoise();

    void toggleSelected(AuroraFile file);
}
