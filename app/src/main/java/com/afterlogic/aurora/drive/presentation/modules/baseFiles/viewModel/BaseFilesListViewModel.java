package com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFilesListViewModel<T extends BaseFileItemViewModel> extends SwipeRefreshLayout.OnRefreshListener{

    ObservableList<T> getItems();

    ObservableBoolean getRefreshing();

    ObservableField<AuroraFile> getCurrentFolder();

    ObservableBoolean getErrorState();
}
