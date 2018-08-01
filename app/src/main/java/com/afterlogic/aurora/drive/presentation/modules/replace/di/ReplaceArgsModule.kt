package com.afterlogic.aurora.drive.presentation.modules.replace.di

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceArgs

import dagger.Module
import dagger.Provides

/**
 * Created by aleksandrcikin on 28.06.17.
 * mail: mail@sunnydaydev.me
 */

@Module
internal class ReplaceArgsModule {

    @Provides
    @ModuleScope
    fun provideArgsInitializer() = ReplaceArgs.Initializer()


    @Provides
    fun provideArgs(initializer: ReplaceArgs.Initializer) = initializer.args

}
