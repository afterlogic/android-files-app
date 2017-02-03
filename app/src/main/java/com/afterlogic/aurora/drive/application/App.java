package com.afterlogic.aurora.drive.application;

import android.app.Application;

import com.afterlogic.aurora.drive.application.assembly.ApplicationAssemblyComponent;
import com.afterlogic.aurora.drive.application.assembly.ApplicationAssemblyModule;
import com.afterlogic.aurora.drive.application.assembly.DaggerApplicationAssemblyComponent;
import com.afterlogic.aurora.drive.application.configurators.application.ApplicationConfigurationCallback;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;


/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class App extends Application implements ApplicationConfigurationCallback {

    //Presentation modules's factory
    private ModulesFactoryComponent mModulesFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        configureApp();
    }

    /**
     * {@link ApplicationConfigurationCallback#onWireframeFactoryConfigured(ModulesFactoryComponent)}  implementation.
     *
     * When app is success configured modulesStore presentation modules's factory.
     */
    @Override
    public void onWireframeFactoryConfigured(ModulesFactoryComponent component) {
        mModulesFactory = component;
    }

    public ModulesFactoryComponent modulesFactory(){
        return mModulesFactory;
    }

    /**
     * Init dagger and all components and modules. Configure application and third parties apps.
     */
    private void configureApp(){
        ApplicationAssemblyComponent application = DaggerApplicationAssemblyComponent.builder()
                .applicationAssemblyModule(new ApplicationAssemblyModule(this))
                .build();

        application.applicationConfigurator().config();
        application.thirdPartiesConfigurator().config();
    }
}
