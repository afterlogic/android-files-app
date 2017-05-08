package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
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

    public static Intent intent(boolean restartTask, Context context) {
        if (restartTask) {
            return IntentCompat.makeRestartActivityTask(new ComponentName(context, LoginActivity.class));
        } else {
            return new Intent(context, LoginActivity.class);
        }
    }

    @Override
    public void assembly(InjectorsComponent modulesFactory) {
        modulesFactory.login().inject(this);
    }

    @Override
    protected void onPerformCreate(@Nullable Bundle savedInstanceState) {
        super.onPerformCreate(savedInstanceState);
        setCheckAuth(false);
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

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().onViewResumed();
    }
}

