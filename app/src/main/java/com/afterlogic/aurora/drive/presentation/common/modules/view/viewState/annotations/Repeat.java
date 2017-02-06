package com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by sashka on 14.01.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Repeat {
    String GROUP_NONE = "none";
    RepeatPolicy value() default RepeatPolicy.NONE;
    String group() default GROUP_NONE;
}
