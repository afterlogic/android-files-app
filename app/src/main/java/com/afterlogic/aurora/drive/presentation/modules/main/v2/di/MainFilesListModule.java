package com.afterlogic.aurora.drive.presentation.modules.main.v2.di;

import android.arch.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel.MainFilesListViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module(includes = MainFilesListProvidesModule.class)
public abstract class MainFilesListModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainFilesListViewModel.class)
    abstract ViewModel bindViewModel(MainFilesListViewModel vm);

}
