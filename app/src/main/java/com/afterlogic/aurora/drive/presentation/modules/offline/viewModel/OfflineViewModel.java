package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface OfflineViewModel extends ViewModel {

    ObservableList<BaseFileItemViewModel> getItems();

    void onItemClicked(BaseFileItemViewModel item);
}
