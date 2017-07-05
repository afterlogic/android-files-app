package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.presentation.common.binding.binder.StringBindable;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;

/**
 * Created by aleksandrcikin on 29.04.17.
 */

public interface LoginViewModel extends ViewModel {
    StringBindable getLogin();

    StringBindable getPassword();

    StringBindable getHost();

    ObservableField<String> getPasswordError();

    ObservableField<String> getLoginError();

    ObservableField<String> getHostError();

    ObservableBoolean getIsInProgress();

    void onLogin();

    void onViewResumed();
}
