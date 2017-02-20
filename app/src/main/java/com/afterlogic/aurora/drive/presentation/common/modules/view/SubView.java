package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;

/**
 * Created by sashka on 02.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Deprecated
public interface SubView extends PresentationView, Stoppable {
    void restoreInstance(Bundle bundle);
    void saveInstance(Bundle bundle);
    void assembly(InjectorsComponent wireframes);
}
