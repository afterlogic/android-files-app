package com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.BaseFilesModel;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFilesViewModel extends SwipeRefreshLayout.OnRefreshListener, ViewModel{

    ObservableList<Storage> getStorages();

    BaseFilesModel getModel();

    ObservableBoolean getRefreshing();

    ObservableField<String> getFolderTitle();

    ObservableBoolean getLocked();

    ObservableInt getCurrentPagePosition();

    ObservableBoolean getErrorState();

    void onCurrentFolderChanged(AuroraFile folder);

}
