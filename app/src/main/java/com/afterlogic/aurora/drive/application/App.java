package com.afterlogic.aurora.drive.application;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.application.assembly.AppInjector;
import com.afterlogic.aurora.drive.core.common.contextWrappers.account.AccountHelper;
import com.afterlogic.aurora.drive.core.common.logging.CrashlyticsLogger;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.logging.ToCrashlyticsLogger;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefs;
import com.afterlogic.aurora.drive.data.modules.prefs.Pref;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction.AccountActionReceiver;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import io.fabric.sdk.android.Fabric;


/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class App extends Application implements HasActivityInjector, HasBroadcastReceiverInjector {

    private static final int APP_UPDATER_VERSION = 1;

    //Presentation modules's factory
    private InjectorsComponent mInjectors;

    @Inject
    AppPrefs appPrefs;

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> receiverInjector;

    @Inject
    ActivityTracker activityTracker;

    @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        AppInjector.inject(this);

        initThirdParties();

        checkAppVersion();

        startService(new Intent(this, FileObserverService.class));

        activityTracker.register(this);

        sessionManager.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AccountManager.get(this).addOnAccountsUpdatedListener(
                    accounts -> AccountActionReceiver.notifyAccountsChanged(App.this),
                    null,
                    true,
                    new String[]{ AccountHelper.ACCOUNT_TYPE }
            );

        }
    }

    /**
     *
     * When app is success configured modulesStore presentation modules's factory.
     */
    public void onInjectorsConfigured(InjectorsComponent component) {
        mInjectors = component;
    }

    public InjectorsComponent getInjectors(){
        return mInjectors;
    }

    private void initThirdParties() {
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();

        Crashlytics crashlytics = new Crashlytics.Builder()
                .core(crashlyticsCore)
                .build();

        Fabric fabric = new Fabric.Builder(this)
                .kits(crashlytics)
                .logger(new CrashlyticsLogger())
                .build();

        Fabric.with(fabric);
        Fabric.getLogger().setLogLevel(BuildConfig.DEBUG ? Log.DEBUG : Log.INFO);

        ToCrashlyticsLogger logger = new ToCrashlyticsLogger();
        MyLog.setLogger(logger);
    }

    private void checkAppVersion() {

        Pref<Integer> appConfigVersion = appPrefs.appConfigVersion();
        int currentAppConfigVersion = appConfigVersion.get();
        if (currentAppConfigVersion != APP_UPDATER_VERSION){
            updateApp(currentAppConfigVersion, APP_UPDATER_VERSION);
            appConfigVersion.set(APP_UPDATER_VERSION);
        }
    }

    private void updateApp(int from, int to){

    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return receiverInjector;
    }
}
