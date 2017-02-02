package com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sashka on 15.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreViewActiveState {
}
