package com.afterlogic.aurora.drive.application.assembly;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.core.assembly.CoreAssemblyModule;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.data.assembly.DataAssemblyComponent;
import com.afterlogic.aurora.drive.data.assembly.DataModule;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyModule;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsModule;
import com.afterlogic.aurora.drive.presentation.assembly.presentation.PresentationAssemblyComponent;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 28.06.17.
 * mail: mail@sunnydaydev.me
 */

public class AppInjector {

    public static void inject(App app) {

        MyLog.d(app, "Configure dagger.");

        ApplicationAssemblyComponent applicationComponent = DaggerApplicationAssemblyComponent.builder()
                .applicationAssemblyModule(new ApplicationAssemblyModule(app))
                .build();

        DataAssemblyComponent dataComponent = applicationComponent
                .plus(new CoreAssemblyModule())
                .plus(new DataModule());

        // Old injection style
        PresentationAssemblyComponent presentationComponent =
                dataComponent.presentationAssembly();

        InjectorsModule modulesModule = new InjectorsModule(
                presentationComponent.plus(new AssembliesAssemblyModule())
        );

        InjectorsComponent modulesComponent = presentationComponent
                .plus(modulesModule);

        app.onInjectorsConfigured(modulesComponent);

        modulesComponent.inject(app);

        // New injection style
//        AutoInjectPresentationComponent component = DaggerAutoInjectPresentationComponent.builder()
//                .dataAssemblyComponent(dataComponent)
//                .build();
//
//        component.inject(app);

        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                handleActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    private static void handleActivity(Activity activity) {
        if (activity instanceof Injectable || activity instanceof HasSupportFragmentInjector) {
            AndroidInjection.inject(activity);
        }
        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {

                        @Override
                        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
                            handleFragment(f);
                        }
                    }, true);
        }
    }

    private static void handleFragment(Fragment fragment) {
        if (fragment instanceof Injectable) {
            AndroidSupportInjection.inject(fragment);
        }
    }
}
