package com.afterlogic.aurora.drive.data.common.mapper;

/**
 * Created by sashka on 01.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface Mapper<R, S> {
    R map(S source);
}
