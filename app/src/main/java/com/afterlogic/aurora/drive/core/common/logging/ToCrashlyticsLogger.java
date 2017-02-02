package com.afterlogic.aurora.drive.core.common.logging;

import android.util.Log;

import com.crashlytics.android.core.CrashlyticsCore;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ToCrashlyticsLogger implements MyLog.Logger{

    @Override
    public void logInfo(String tag, String message) {
        CrashlyticsCore.getInstance().log(Log.INFO, tag, message);
    }

    @Override
    public void logDebug(String tag, String message) {
        CrashlyticsCore.getInstance().log(Log.DEBUG, tag, message);
    }

    @Override
    public void logWarn(String tag, String message) {
        CrashlyticsCore.getInstance().log(Log.WARN, tag, message);
    }

    @Override
    public void logError(String tag, String message) {
        CrashlyticsCore.getInstance().log(Log.ERROR, tag, message);
    }

    @Override
    public void logMajorException(String tag, Throwable error) {
        CrashlyticsCore.getInstance().logException(error);
    }
}
