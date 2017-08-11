package com.afterlogic.aurora.drive.presentation.modules.login.v2.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginViewModel extends LifecycleViewModel {

    public ObservableField<LoginViewModelState> loginState = new ObservableField<>(LoginViewModelState.HOST);

    public Bindable<String> host = Bindable.create();
    public ObservableField<String> hostError = new ObservableField<>();
    public ObservableBoolean isInProgress = new ObservableBoolean();

    @Inject
    public LoginViewModel() {
    }

    public void onHostWritten() {
        loginState.set(LoginViewModelState.LOGIN);
    }
}
