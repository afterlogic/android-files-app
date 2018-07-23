package com.afterlogic.aurora.drive.presentation.modules.upload.di;

import androidx.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFileListViewModel;

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
