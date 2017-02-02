package com.afterlogic.aurora.drive.data.common.mapper;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BiMapper<R, S1, S2> {
    R map(S1 first, S2 second);
}
