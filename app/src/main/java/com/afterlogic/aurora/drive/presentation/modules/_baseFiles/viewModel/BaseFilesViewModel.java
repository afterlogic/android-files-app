package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.support.v4.widget.SwipeRefreshLayout;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.BaseFilesModel;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFilesViewModel extends SwipeRefreshLayout.OnRefreshListener, ViewModel{

    ObservableList<FileType> getFileTypes();

    BaseFilesModel getModel();

    ObservableBoolean getRefreshing();

    ObservableField<String> getFolderTitle();

    ObservableBoolean getLocked();

    ObservableInt getCurrentPagePosition();

    ObservableBoolean getErrorState();

    void onCurrentFolderChanged(AuroraFile folder);
}
