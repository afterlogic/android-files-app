package com.afterlogic.aurora.drive.core.common.streams;

import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandrcikin on 30.05.17.
 * mail: mail@sunnydaydev.me
 */

public class Streams {

    public static Stream<View> streamOfChilds(ViewGroup group) {
        List<View> childs = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); i++) {
            childs.add(group.getChildAt(i));
        }

        return Stream.of(childs);
    }
}
