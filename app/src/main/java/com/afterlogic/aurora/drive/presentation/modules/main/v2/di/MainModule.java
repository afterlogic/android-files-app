package com.afterlogic.aurora.drive.presentation.modules.main.v2.di;

import android.arch.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.view.MainFileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.view.MainFilesActionBottomSheet;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel.MainViewModel;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.di.MainFilesActionModule;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module(includes = MainProvidesModule.class)
public abstract class MainModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindViewModel(MainViewModel vm);

    @ContributesAndroidInjector(modules = MainFilesListModule.class)
    @SubModuleScope
    abstract MainFileListFragment contributeFragment();

    @ContributesAndroidInjector(modules = MainFilesActionModule.class)
    abstract MainFilesActionBottomSheet contributeFileActions();
}
