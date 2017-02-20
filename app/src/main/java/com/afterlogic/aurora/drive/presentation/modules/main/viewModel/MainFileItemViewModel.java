package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFileItemModel;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFileItemViewModel extends BaseFileItemViewModel{
    MainFileItemModel getModel();
    ObservableBoolean getSelected();
    ObservableInt getProgress();
    ObservableBoolean getOffline();
}
