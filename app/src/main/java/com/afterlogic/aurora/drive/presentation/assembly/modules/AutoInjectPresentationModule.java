package com.afterlogic.aurora.drive.presentation.assembly.modules;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.modules._util.BackToNullActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.di.LoginModule;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginFragment;
import com.afterlogic.aurora.drive.presentation.modules.main.di.MainModule;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.di.OfflineModule;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;
import com.afterlogic.aurora.drive.presentation.modules.replace.di.ReplaceModule;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.di.UploadModule;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadActivity;

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

    @ModuleScope
    @ContributesAndroidInjector(modules = UploadModule.class)
    abstract UploadActivity contributeUpload();


    @ContributesAndroidInjector
    abstract LoginActivity bindLoginActivity();

    @ModuleScope
    @ContributesAndroidInjector(modules = LoginModule.class)
    abstract LoginFragment bindLoginFragment();
}
