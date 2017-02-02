package com.afterlogic.aurora.drive._unrefactored.core.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by sashka on 28.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class NumberUtil {

    public static int parseInt(String string, int defaultValue){
        if (TextUtils.isEmpty(string)) return defaultValue;

        try{
            return Integer.parseInt(string);
        } catch (NumberFormatException e){
            return defaultValue;
        }
    }

    public static long parseLong(@NonNull String string, long defaultValue){
        if (TextUtils.isEmpty(string)) return defaultValue;

        try{
            return Long.parseLong(string);
        } catch (NumberFormatException e){
            return defaultValue;
        }
    }

    public static boolean parseBoolean(@NonNull String string, boolean defaultValue){
        if (TextUtils.isEmpty(string)) return defaultValue;

        try{
            return Boolean.parseBoolean(string);
        } catch (NumberFormatException e){
            return defaultValue;
        }
    }
}
