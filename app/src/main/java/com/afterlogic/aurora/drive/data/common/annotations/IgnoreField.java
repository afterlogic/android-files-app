package com.afterlogic.aurora.drive.data.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by aleksandrcikin on 31.05.17.
 * mail: mail@sunnydaydev.me
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreField {

    boolean serealization() default true;
    boolean deserealization() default true;
}
