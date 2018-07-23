package com.afterlogic.aurora.drive.presentation.modules.upload.di;

import androidx.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */
@Module
public abstract class UploadModule {

    @Binds
    @IntoMap
    @ViewModelKey(UploadViewModel.class)
    abstract ViewModel bindViewModel(UploadViewModel vm);

    @SubModuleScope
    @ContributesAndroidInjector(modules = UploadFilesListModule.class)
    abstract UploadFilesListFragment contributeFilesList();
}
