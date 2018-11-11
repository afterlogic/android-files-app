package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.di;

import androidx.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.viewModel.MainFilesActionViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public abstract class MainFilesActionModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainFilesActionViewModel.class)
    abstract ViewModel bindViewModel(MainFilesActionViewModel vm);
}
