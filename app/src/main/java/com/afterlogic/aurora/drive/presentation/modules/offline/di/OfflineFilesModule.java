package com.afterlogic.aurora.drive.presentation.modules.offline.di;

import android.arch.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineFileListViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public abstract class OfflineFilesModule {

    @Binds
    @IntoMap
    @ViewModelKey(OfflineFileListViewModel.class)
    abstract ViewModel bindViewModel(OfflineFileListViewModel vm);
}
