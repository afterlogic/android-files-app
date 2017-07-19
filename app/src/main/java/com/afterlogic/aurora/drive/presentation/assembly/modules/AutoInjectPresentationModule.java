package com.afterlogic.aurora.drive.presentation.assembly.modules;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.modules._util.BackToNullActivity;
import com.afterlogic.aurora.drive.presentation.modules.main.di.MainModule;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.v2.di.OfflineModule;
import com.afterlogic.aurora.drive.presentation.modules.offline.v2.view.OfflineActivity;
import com.afterlogic.aurora.drive.presentation.modules.replace.di.ReplaceModule;
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

    @ContributesAndroidInjector
    abstract BackToNullActivity contributeBackToNull();

    @ModuleScope
    @ContributesAndroidInjector(modules = ReplaceModule.class)
    abstract ReplaceActivity contributeReplace();

    @ModuleScope
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity contributeMain();

    @ModuleScope
    @ContributesAndroidInjector(modules = OfflineModule.class)
    abstract OfflineActivity contributeOffline();
}
