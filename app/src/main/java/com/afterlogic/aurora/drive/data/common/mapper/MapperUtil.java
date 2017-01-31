package com.afterlogic.aurora.drive.data.common.mapper;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collection;
import java.util.List;

/**
 * Created by sashka on 15.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class MapperUtil {

    public static <R, S>  Mapper<List<R>, Collection<S>> collection(Mapper<R, S> mapper){
        return source -> Stream.of(source).map(mapper::map).collect(Collectors.toList());
    }
}
