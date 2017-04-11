package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesViewModel;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesViewModel extends BaseFilesViewModel{

    ObservableList<FileType> getFileTypes();

    ObservableField<String> getLogin();

    ObservableBoolean getMultichoiseMode();

    ObservableInt getSelectedCount();

    ObservableBoolean getSelectedHasFolder();

    void onOfflineModeSelected();

    void onAbout();

    void onLogout();

    void onMultiChoiseAction();

    void setMultichoiseMode(boolean multichoiseMode);

    void setSelectedCount(int count);

    void setSetSelectedHasFolder(boolean hasFolder);

    void onCurrentFolderChanged(AuroraFile folder);

    boolean onBackPressed();
}
