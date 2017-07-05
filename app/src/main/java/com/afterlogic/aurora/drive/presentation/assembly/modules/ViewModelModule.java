package com.afterlogic.aurora.drive.presentation.assembly.modules;

import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory viewModelFactory(AuroraViewModelFactory factory);

}
