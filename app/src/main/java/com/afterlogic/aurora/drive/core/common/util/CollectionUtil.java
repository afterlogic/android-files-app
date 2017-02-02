package com.afterlogic.aurora.drive.core.common.util;

import com.afterlogic.aurora.drive.core.common.interfaces.Equaler;
import com.annimon.stream.Stream;

import java.util.Collection;

/**
 * Created by sashka on 30.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class CollectionUtil {

    public static <T> boolean equals(Collection<T> first, Collection<T> second, Equaler<T> equaler){
        if (first == null && second == null) return true;
        if (first == null || second == null) return false;
        if (first.size() != second.size()) return false;

        return Stream.zip(
                first.iterator(),
                second.iterator(),
                (value1, value2) -> value1 == null && value2 == null ||
                        !(value1 == null || value2 == null) && equaler.equals(value1, value2)
        ).allMatch(value -> value);
    }
}
