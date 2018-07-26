package com.afterlogic.aurora.drive.presentation.modules.replace.di;

import androidx.lifecycle.ViewModel;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileTypeViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public abstract class ReplaceFileTypeModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReplaceFileTypeViewModel.class)
    abstract ViewModel bindReplaceFileTypeViewModel(ReplaceFileTypeViewModel vm);

}
