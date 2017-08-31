package com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sunny on 31.08.17.
 */

@Retention(RUNTIME)
@Target(PARAMETER)
public @interface FormatHeader {
    String value();
}
