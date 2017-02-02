package com.afterlogic.aurora.drive.presentation.assembly.wireframes;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * See {@link WireframeFactoryModule}.
 */
@Subcomponent(modules = {WireframeFactoryModule.class})
public interface ModulesFactoryComponent {

    PresentationModulesStore store();

}
