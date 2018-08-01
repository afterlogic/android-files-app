package com.afterlogic.aurora.drive.presentation.modules.replace.di

import androidx.lifecycle.ViewModel

import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceFileListViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module
abstract class ReplaceFileTypeModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReplaceFileListViewModel::class)
    internal abstract fun bindReplaceFileTypeViewModel(vm: ReplaceFileListViewModel): ViewModel

}
