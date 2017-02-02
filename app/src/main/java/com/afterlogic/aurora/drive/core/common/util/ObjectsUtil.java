package com.afterlogic.aurora.drive.core.common.util;

/**
 * Created by sashka on 16.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ObjectsUtil {

    public static boolean nonNull(Object o){
        return o != null;
    }

    public static boolean equals(Object first, Object second) {
        return first == second || first != null && first.equals(second);
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
