package com.afterlogic.aurora.drive.core.common.annotation.scopes;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by sashka on 26.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Scope
@Inherited
@Retention(RUNTIME)
public @interface DataScope {
}
