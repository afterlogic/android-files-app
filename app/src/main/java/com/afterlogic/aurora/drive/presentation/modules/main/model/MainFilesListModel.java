package com.afterlogic.aurora.drive.presentation.modules.main.model;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.BaseFilesListModel;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

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

    void setFileForActions(AuroraFile file);

    AuroraFile getFileForActions();

    void clearSyncProgress();

    void setSyncProgress(SyncProgress progress);

    void setOffline(AuroraFile file, boolean offline);

    void setActionsEnabled(boolean enabled);
}
