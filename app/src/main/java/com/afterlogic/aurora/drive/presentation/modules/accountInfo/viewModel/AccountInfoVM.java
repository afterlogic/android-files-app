package com.afterlogic.aurora.drive.presentation.modules.accountInfo.viewModel;

import androidx.databinding.ObservableField;

import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface AccountInfoVM extends ViewModel {

    ObservableField<String> getLogin();

    ObservableField<String> getHost();
}
