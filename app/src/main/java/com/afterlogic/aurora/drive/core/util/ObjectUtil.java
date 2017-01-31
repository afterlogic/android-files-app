package com.afterlogic.aurora.drive.core.util;

/**
 * Created by sashka on 31.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class ObjectUtil {

    public static boolean nonNull(Object o){
        return o != null;
    }

    public static boolean allNull(Object... objects){
        for (Object o:objects){
            if (o != null){
                return false;
            }
        }
        return true;
    }

    public static boolean hasNull(Object... objects){
        for (Object o:objects){
            if (o != null){
                return false;
            }
        }
        return true;
    }
}
