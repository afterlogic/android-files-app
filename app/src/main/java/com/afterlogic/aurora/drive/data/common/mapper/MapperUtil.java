package com.afterlogic.aurora.drive.data.common.mapper;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by sashka on 15.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class MapperUtil {

    public static <R, S> Mapper<List<R>, Collection<S>> list(Mapper<R, S> mapper){
        return source -> Stream.of(source).map(mapper::map).collect(Collectors.toList());
    }

    public static <R, S> Mapper<List<R>, Collection<S>> listOrNull(Mapper<R, S> mapper){
        return source -> {
            if (source == null) return null;
            return list(mapper).map(source);
        };
    }

    public static <R, S> Mapper<List<R>, Collection<S>> listOrEmpty(Mapper<R, S> mapper){
        return source -> {
            if (source == null || source.size() == 0) return Collections.emptyList();
            return list(mapper).map(source);
        };
    }

    public static <R, S> List<R> listOrNull(Collection<S> source, Mapper<R, S> mapper){
        return listOrNull(mapper).map(source);
    }

    public static <R, S> List<R> listOrEmpty(Collection<S> source, Mapper<R, S> mapper){
        return listOrEmpty(mapper).map(source);
    }
}
