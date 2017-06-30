package com.afterlogic.aurora.drive.application.assembly;

import android.content.Context;

import com.afterlogic.aurora.drive.application.App;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Root Dagger component.
 * Provide application context, configurators.
 */
@Module
public class ApplicationAssemblyModule {

    private App mAppContext;

    public ApplicationAssemblyModule(App appContext) {
        mAppContext = appContext;
    }

    @Provides
    Context provideApplicationContext(){
        return mAppContext;
    }

    @Provides
    App application(){
        return mAppContext;
    }

}
