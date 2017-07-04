package com.afterlogic.aurora.drive.presentation.assembly.modules;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.modules.replace.assembly.ReplaceModule;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceActivity;

import dagger.Module;
import dagger.android.AndroidInjectionModule;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */
@Module(includes = {
        AndroidInjectionModule.class,
        ViewModelModule.class
})
public abstract class AutoInjectPresentationModule {

    @ModuleScope
    @ContributesAndroidInjector(modules = ReplaceModule.class)
    abstract ReplaceActivity contributeReplace();
}
