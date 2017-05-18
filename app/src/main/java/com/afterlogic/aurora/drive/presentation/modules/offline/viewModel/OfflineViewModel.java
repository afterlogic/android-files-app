package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog.MessageDialogViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.dialog.ProgressDialogViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface OfflineViewModel extends ViewModel, SwipeRefreshLayout.OnRefreshListener {

    void viewInitWith(boolean manualMode);

    @NonNull
    ObservableList<BaseFileItemViewModel> getItems();

    @NonNull
    ObservableBoolean getManualMode();

    @NonNull
    ObservableBoolean getRefreshing();

    @NonNull
    ObservableField<MessageDialogViewModel> getMessage();

    @NonNull
    ObservableField<ProgressDialogViewModel> getProgress();

    @NonNull
    ObservableBoolean getNetworkState();

    void onItemClicked(BaseFileItemViewModel item);

    void onOnline();
}
