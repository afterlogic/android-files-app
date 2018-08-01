package com.afterlogic.aurora.drive.presentation.modules.replace.di

import androidx.lifecycle.ViewModel

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope
import com.afterlogic.aurora.drive.presentation.assembly.modules.ViewModelKey
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceFileTypeFragment
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceViewModel

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by aleksandrcikin on 28.06.17.
 * mail: mail@sunnydaydev.me
 */

@Module(includes = arrayOf(ReplaceArgsModule::class))
abstract class ReplaceModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReplaceViewModel::class)
    internal abstract fun bindReplaceViewModel(vm: ReplaceViewModel): ViewModel

    @SubModuleScope
    @ContributesAndroidInjector(modules = arrayOf(ReplaceFileTypeModule::class))
    internal abstract fun bindReplaceFragment(): ReplaceFileTypeFragment

}
