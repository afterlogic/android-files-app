package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.inputmethod.EditorInfo;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.ActivityLoginBinding;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModel;

/**
 * A checkApiVersion screen that offers checkApiVersion via email/password.
 */
public class LoginActivity extends MVVMActivity<LoginViewModel> {

    @Override
    public void assembly(InjectorsComponent modulesFactory) {
        modulesFactory.login().inject(this);
    }

    @NonNull
    @Override
    protected ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState) {
        return DataBindingUtil.setContentView(this, R.layout.activity_login);
    }

    @Override
    protected void onBindingCreated(@Nullable Bundle savedInstanceState) {
        super.onBindingCreated(savedInstanceState);
        ActivityLoginBinding binding = getBinding();

        setSupportActionBar(binding.toolbar);

        binding.password.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                getViewModel().onLogin();
                return true;
            }
            return false;
        });
    }
}

