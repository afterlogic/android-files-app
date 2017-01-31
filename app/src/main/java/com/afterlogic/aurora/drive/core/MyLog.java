package com.afterlogic.aurora.drive.core;

import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.core.CrashlyticsCore;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Own logger implementation for configuring log in future.
 */
public class MyLog {

    public static void d(Object tag, String message){
        debugLog(getTag(tag), message);
    }

    public static void e(Object tag, String message){
        errorLog(getTag(tag), message);
    }

    public static void e(Object tag, Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        errorLog(getTag(tag), sw.toString());
    }

    public static void e(Object tag, String message, Throwable error){
        e(tag, message);
        e(tag, error);
    }

    public static void majorException(Object tag, Throwable e){
        e(tag, e);
        CrashlyticsCore.getInstance().logException(e);
    }

    public static void majorException(Object tag, String errorMessage){
        Error e = new Error(errorMessage);
        majorException(tag, e);
    }

    private static void debugLog(String tag, String message){
        CrashlyticsCore.getInstance().log(Log.DEBUG, tag, message);
    }

    private static void errorLog(String tag, String message){
        CrashlyticsCore.getInstance().log(Log.ERROR, tag, message);
    }

    private static String getTag(Object tag){
        if (tag instanceof String || tag instanceof CharSequence || tag instanceof Number){
            return  String.valueOf(tag);
        } else {
            String tagString = tag.getClass().getSimpleName();
            if (!TextUtils.isEmpty(tagString)) {
                return tagString;
            } else {
                return "???";
            }
        }
    }
}
