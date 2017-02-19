package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base module assembly component with default di injection.
 */
@Deprecated
public interface PresentationComponent<View extends PresentationView, Target extends View> {
    void inject(Target target);

    PresentationModule<View> module();
}