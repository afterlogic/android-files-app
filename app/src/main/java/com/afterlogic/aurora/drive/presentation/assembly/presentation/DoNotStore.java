package com.afterlogic.aurora.drive.presentation.assembly.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sashka on 30.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoNotStore {
}
