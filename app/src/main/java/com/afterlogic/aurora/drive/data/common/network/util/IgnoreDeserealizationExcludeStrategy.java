package com.afterlogic.aurora.drive.data.common.network.util;

import com.afterlogic.aurora.drive.data.common.annotations.IgnoreField;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Created by aleksandrcikin on 31.05.17.
 * mail: mail@sunnydaydev.me
 */

public class IgnoreDeserealizationExcludeStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        IgnoreField ignoreField = f.getAnnotation(IgnoreField.class);
        return ignoreField != null && ignoreField.deserealization();
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
