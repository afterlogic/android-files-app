package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.v4.widget.SwipeRefreshLayout;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.BaseFilesListModel;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFilesListViewModel<T extends BaseFileItemViewModel> extends SwipeRefreshLayout.OnRefreshListener{

    BaseFilesListModel getModel();

    ObservableList<T> getItems();

    ObservableBoolean getRefreshing();

    ObservableField<AuroraFile> getCurrentFolder();

    ObservableBoolean getErrorState();
}
