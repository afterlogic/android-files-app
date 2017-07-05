package com.afterlogic.aurora.drive.presentation.modules.replace.assembly;

import android.arch.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceFileTypeFragment;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 28.06.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public abstract class ReplaceModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReplaceViewModel.class)
    abstract ViewModel bindReplaceViewModel(ReplaceViewModel vm);

    @SubModuleScope
    @ContributesAndroidInjector(modules = ReplaceFileTypeModule.class)
    abstract ReplaceFileTypeFragment bindReplaceFragment();
}
