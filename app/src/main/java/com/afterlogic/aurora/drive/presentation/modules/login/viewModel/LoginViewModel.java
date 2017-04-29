package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.presentation.common.binding.binder.StringBinder;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;

/**
 * Created by aleksandrcikin on 29.04.17.
 */

public interface LoginViewModel extends ViewModel {
    StringBinder getLogin();

    StringBinder getPassword();

    StringBinder getHost();

    ObservableField<String> getPasswordError();

    ObservableField<String> getLoginError();

    ObservableField<String> getHostError();

    ObservableBoolean getIsInProgress();

    void onLogin();
}
