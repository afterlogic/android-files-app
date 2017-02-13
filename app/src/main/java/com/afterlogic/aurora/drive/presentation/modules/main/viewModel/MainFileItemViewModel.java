package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;

import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemViewModel;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFileItemViewModel extends BaseFileItemViewModel{
    ObservableBoolean getSelected();
}
