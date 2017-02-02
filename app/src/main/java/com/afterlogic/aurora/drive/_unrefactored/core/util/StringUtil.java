package com.afterlogic.aurora.drive._unrefactored.core.util;

/**
 * Created by sashka on 23.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class StringUtil {
    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
