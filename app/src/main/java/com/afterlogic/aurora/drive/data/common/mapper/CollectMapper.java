package com.afterlogic.aurora.drive.data.common.mapper;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface CollectMapper<T, F, S> {
    T map(F first, S second);
}
