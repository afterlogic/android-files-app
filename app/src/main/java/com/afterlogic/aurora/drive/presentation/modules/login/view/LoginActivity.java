package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.ActivityLoginBinding;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.login.presenter.LoginPresenter;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModel;

import javax.inject.Inject;

/**
 * A checkApiVersion screen that offers checkApiVersion via email/password.
 */
public class LoginActivity extends BaseActivity implements LoginView{

    public static final String EXTRA_NEXT_ACTIVITY =
            LoginActivity.class.getName() + ".EXTRA_NEXT_ACTIVITY";
    public static final String EXTRA_FINISH_ON_RESULT =
            LoginActivity.class.getName() + "EXTRA_FINISH_ON_RESULT";

    @Inject @ViewPresenter
    protected LoginPresenter mPresenter;

    @Inject
    protected LoginViewModel mViewModel;

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.login().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setSupportActionBar(binding.toolbar);

        binding.setViewModel(mViewModel);

        binding.password.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                mViewModel.onLogin();
                return true;
            }
            return false;
        });
    }

    public static class IntentCreator{

        public static Intent makeNextActivity(Intent i, Class activityClass){
            i.putExtra(EXTRA_NEXT_ACTIVITY, activityClass);
            return i;
        }

        public static Intent loginAndReturn(Intent i){
            i.putExtra(EXTRA_FINISH_ON_RESULT, true);
            return i;
        }
    }
}

