package com.afterlogic.aurora.drive.presentation.assembly.wireframes;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * See {@link ModulesFactoryModule}.
 */
@Subcomponent(modules = {ModulesFactoryModule.class})
public interface ModulesFactoryComponent {

    PresentationModulesStore store();

    Injector<LoginActivity> login();

}
