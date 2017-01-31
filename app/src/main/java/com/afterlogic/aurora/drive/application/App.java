package com.afterlogic.aurora.drive.application;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.data.assembly.AppDataComponent;
import com.afterlogic.aurora.drive.data.assembly.DaggerAppDataComponent;
import com.afterlogic.aurora.drive.data.assembly.DataModule;
import com.afterlogic.aurora.drive.data.common.ApiProvider;
import com.afterlogic.aurora.drive.data.common.api.Api;
import com.afterlogic.aurora.drive.presentation.services.ClearCacheService;
import com.afterlogic.aurora.drive.presentation.services.FileObserverService;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by sashka on 22.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class App extends Application{


    private AppDataComponent mApiComponent;

    public AppDataComponent getDataComponent(){
        return mApiComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApiComponent = DaggerAppDataComponent.builder()
                .dataModule(new DataModule(this))
                .build();

        //[START Init Fabric.Crashlytics]
        CrashlyticsCore core = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG).build();
        Crashlytics crashlytics = new Crashlytics.Builder()
                .core(core).build();
        Fabric.with(this, crashlytics);
        Fabric.getLogger().setLogLevel(BuildConfig.DEBUG ? Log.VERBOSE : Log.INFO);
        //[END Init Fabric.Crashlytics]

        ApiProvider apiProvider = new ApiProvider();
        getDataComponent().inject(apiProvider);

        Api.init(this, apiProvider);

        startService(new Intent(this, ClearCacheService.class));
        startService(new Intent(this, FileObserverService.class));
    }
}
