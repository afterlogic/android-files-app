package com.afterlogic.aurora.drive.presentation.modules.offline.di;

import android.arch.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineFragment;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public abstract class OfflineModule {

    @Binds
    @IntoMap
    @ViewModelKey(OfflineViewModel.class)
    abstract ViewModel bindViewModel(OfflineViewModel vm);

    @SubModuleScope
    @ContributesAndroidInjector(modules = OfflineFilesModule.class)
    abstract OfflineFragment contributeOfflineFragment();
}
