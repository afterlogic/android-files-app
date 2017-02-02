package com.afterlogic.aurora.drive.application.configurators.thirdParties;

import android.content.Context;
import android.util.Log;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.core.common.interfaces.Configurable;
import com.afterlogic.aurora.drive.core.common.logging.CrashlyticsLogger;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.logging.ToCrashlyticsLogger;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Third parties library configurator.
 */
public class ThirdPartiesConfigurator implements Configurable {

    private Context mContext;

    @SuppressWarnings("WeakerAccess")
    @Inject public ThirdPartiesConfigurator(Context context) {
        mContext = context;
    }

    @Override
    public void config() {
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();

        Crashlytics crashlytics = new Crashlytics.Builder()
                .core(crashlyticsCore)
                .build();

        Fabric fabric = new Fabric.Builder(mContext)
                .kits(crashlytics)
                .logger(new CrashlyticsLogger())
                .build();

        Fabric.with(fabric);
        Fabric.getLogger().setLogLevel(BuildConfig.DEBUG ? Log.DEBUG : Log.INFO);

        ToCrashlyticsLogger logger = new ToCrashlyticsLogger();
        MyLog.setLogger(logger);
    }
}
