package com.afterlogic.aurora.drive.application;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.application.assembly.AppInjector;
import com.afterlogic.aurora.drive.core.common.contextWrappers.account.AccountHelper;
import com.afterlogic.aurora.drive.core.common.logging.CrashlyticsLogger;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.logging.ToCrashlyticsLogger;
import com.afterlogic.aurora.drive.core.common.util.AppUtil;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.cleaner.DataCleaner;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefs;
import com.afterlogic.aurora.drive.data.modules.prefs.Pref;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction.AccountActionReceiver;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import javax.inject.Inject;

import androidx.multidex.MultiDex;
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

    private static final int APP_UPDATER_VERSION = 2;

    //Presentation modules's factory
    private InjectorsComponent mInjectors;

    @Inject
    protected AppPrefs appPrefs;

    @Inject
    protected DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    protected DispatchingAndroidInjector<BroadcastReceiver> receiverInjector;

    @Inject
    protected ActivityTracker activityTracker;

    @Inject
    protected SessionManager sessionManager;

    @Inject
    protected DataCleaner dataCleaner;

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppInjector.inject(this);

        checkAppVersion();

        initThirdParties();

        startService(new Intent(this, FileObserverService.class));

        activityTracker.register(this);

        sessionManager.start();

        AuroraSession session = sessionManager.getSession();
        if (session == null) {
            stopService(new Intent(this, FileObserverService.class));
            AppUtil.setComponentEnabled(FileObserverService.class, false, this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AccountManager.get(this).addOnAccountsUpdatedListener(
                    accounts -> AccountActionReceiver.notifyAccountsChanged(App.this),
                    null,
                    true,
                    new String[]{ AccountHelper.ACCOUNT_TYPE }
            );

        }
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return receiverInjector;
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

        if (currentAppConfigVersion != APP_UPDATER_VERSION) {

            if (currentAppConfigVersion != 0) {
                updateApp(currentAppConfigVersion);
            }

            appConfigVersion.set(APP_UPDATER_VERSION);

        }

    }

    private void updateApp(int from){

        if (from < 2) {

            Throwable error = dataCleaner.cleanAllUserData()
                    .blockingGet();

            if (error != null) {
                MyLog.majorException(error);
            }

        }

    }
}
