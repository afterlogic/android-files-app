package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.assembly.Injectable;
import com.afterlogic.aurora.drive.databinding.GeneralContentContainerBinding;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.AppCoreActivity;
import com.afterlogic.aurora.drive.presentation.common.util.IntentUtil;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginActivity extends AppCoreActivity implements Injectable, HasSupportFragmentInjector {

    public static final String KEY_RELOGIN = "relogin";
    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    private GeneralContentContainerBinding binding;

    public static Intent intent(boolean relogin) {
        return IntentUtil.intent(LoginActivity.class)
                .putExtra(KEY_RELOGIN, relogin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(
                this, R.layout.general_content_container
        );

        if (savedInstanceState == null) {

            FragmentManager fm = getSupportFragmentManager();

            boolean relogin = getIntent().getBooleanExtra(KEY_RELOGIN, false);

            fm.beginTransaction()
                    .add(binding.contentContainer.getId(), LoginFragment.newInstance(relogin))
                    .commit();
            fm.executePendingTransactions();

        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();

        LoginFragment fragment = (LoginFragment) fm.findFragmentById(binding.contentContainer.getId());

        if (fragment != null) {
            fragment.onBackPressed();
        }
    }
}
