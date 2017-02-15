package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.model.FilesSelection;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListViewModel;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFileListViewModel extends BaseFilesListViewModel<MainFileItemViewModel>{

    MainFilesListModel getModel();
    ObservableField<FilesSelection> getSelection();
    ObservableField<MainFileItemViewModel> getFileRequeireActions();

    void onCancelFileActions();
}
