package com.afterlogic.aurora.drive.presentation.assembly;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base wireframe.
 */
public interface Injector<T>{

    /**
     * Set wireframe target.
     */
    void inject(T target);
}
