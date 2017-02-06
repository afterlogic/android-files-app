package com.afterlogic.aurora.drive._unrefactored.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by sashka on 21.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class ClassUtil {

    /**
     * Get type generic of interface (first if more than one)
     * @param o - interface object
     */
    public static Type getGenericType(Object o, int index){
        Type[] genericInterfaces = o.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                return genericTypes[index];
            }
        }
        return null;
    }
}
