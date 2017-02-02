package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base wireframe.
 */
public interface Injector<T extends PresentationView>{

    /**
     * Set wireframe target.
     */
    void inject(T target);
}
