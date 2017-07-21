package com.afterlogic.aurora.drive.presentation.modules.upload.v2.di;

import android.arch.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel.UploadFileListViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public abstract class UploadFilesListModule {

    @Binds
    @IntoMap
    @ViewModelKey(UploadFileListViewModel.class)
    abstract ViewModel bindViewModel(UploadFileListViewModel vm);
}
