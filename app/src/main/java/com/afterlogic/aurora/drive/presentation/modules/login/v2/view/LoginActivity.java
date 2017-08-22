package com.afterlogic.aurora.drive.presentation.modules.login.v2.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.assembly.Injectable;
import com.afterlogic.aurora.drive.databinding.GeneralContentContainerBinding;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.AppCoreActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginActivity extends AppCoreActivity implements Injectable, HasSupportFragmentInjector {

    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    private GeneralContentContainerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(
                this, R.layout.general_content_container
        );

        if (savedInstanceState == null) {

            FragmentManager fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .add(binding.contentContainer.getId(), LoginFragment.newInstance())
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
