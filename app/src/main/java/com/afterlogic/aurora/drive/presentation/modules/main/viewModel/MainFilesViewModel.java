package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFilesModel;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesViewModel extends BaseFilesViewModel{

    ObservableList<FileType> getFileTypes();

    MainFilesModel getModel();

    ObservableField<String> getLogin();

    ObservableBoolean getMultichoiseMode();

    ObservableInt getSelectedCount();

    ObservableBoolean getSelectedHasFolder();

    void onOfflineModeSelected();
}
