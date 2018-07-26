package com.afterlogic.aurora.drive.presentation.modules.main.di;

import androidx.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesListViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module()
public abstract class MainFilesListModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainFilesListViewModel.class)
    abstract ViewModel bindViewModel(MainFilesListViewModel vm);

}
