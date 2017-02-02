package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;

/**
 * Created by sashka on 02.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface SubView extends PresentationView, Stoppable {
    void restoreInstance(Bundle bundle);
    void saveInstance(Bundle bundle);
    void assembly(ModulesFactoryComponent wireframes);
}
