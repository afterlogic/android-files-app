package com.afterlogic.aurora.drive.application.configurators.application;

import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;
import com.afterlogic.aurora.drive.application.assembly.ApplicationAssemblyComponent;
import com.afterlogic.aurora.drive.core.assembly.CoreAssemblyModule;
import com.afterlogic.aurora.drive.core.common.interfaces.Configurable;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.data.assembly.DataAssemblyComponent;
import com.afterlogic.aurora.drive.data.assembly.DataModule;
import com.afterlogic.aurora.drive.data.modules.prefs.Pref;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyModule;
import com.afterlogic.aurora.drive.presentation.assembly.presentation.PresentationAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.assembly.presentation.PresentationAssemblyModule;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsModule;

import javax.inject.Inject;


/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Application configurator. Configure dagger dependencies.
 */
public class ApplicationConfigurator implements Configurable {

    private static final int APP_UPDATER_VERSION = 1;

    private ApplicationAssemblyComponent mApplicationComponent;
    private ApplicationConfigurationCallback mConfigurationCallback;

    private final Context mContext;

    @SuppressWarnings("WeakerAccess")
    @Inject public ApplicationConfigurator(Context application, ApplicationAssemblyComponent component) {
        mConfigurationCallback = (ApplicationConfigurationCallback) application;
        mApplicationComponent = component;
        mContext = application;
    }

    @Override
    public void config() {
        MyLog.d(this, "Configure app.");
        configDagger();
    }

    private void configDagger(){
        MyLog.d(this, "Configure dagger.");

        DataAssemblyComponent dataComponent = mApplicationComponent
                .plus(new CoreAssemblyModule())
                .plus(new DataModule());

        Pref<Integer> appConfigVersion = dataComponent.prefs().appConfigVersion();
        int currentAppConfigVersion = appConfigVersion.get();
        if (currentAppConfigVersion != APP_UPDATER_VERSION){
            updateApp(currentAppConfigVersion, APP_UPDATER_VERSION, dataComponent);
            appConfigVersion.set(APP_UPDATER_VERSION);
        }

        PresentationAssemblyComponent presentationComponent =
                dataComponent.plus(new PresentationAssemblyModule());

        InjectorsModule modulesModule = new InjectorsModule(
                presentationComponent.plus(new AssembliesAssemblyModule())
        );

        InjectorsComponent modulesComponent = presentationComponent
                .plus(modulesModule);

        mContext.startService(new Intent(mContext, FileObserverService.class));

        mConfigurationCallback.onInjectorsConfigured(modulesComponent);
    }

    private void updateApp(int from, int to, DataAssemblyComponent data){

    }
}
