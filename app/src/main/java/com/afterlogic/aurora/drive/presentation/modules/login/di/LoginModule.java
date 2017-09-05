package com.afterlogic.aurora.drive.presentation.modules.login.di;

import android.arch.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.login.view.AuthFragment;
import com.afterlogic.aurora.drive.presentation.modules.login.view.HostFragment;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public abstract class LoginModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindViewModel(LoginViewModel vm);

    @ContributesAndroidInjector
    abstract HostFragment bindHost();

    @ContributesAndroidInjector
    abstract AuthFragment bindAuth();
}
