package com.afterlogic.aurora.drive.application.assembly;

import com.afterlogic.aurora.drive.core.assembly.CoreAssemblyComponent;
import com.afterlogic.aurora.drive.core.assembly.CoreAssemblyModule;
import com.afterlogic.aurora.drive.core.common.annotation.qualifer.Application;
import com.afterlogic.aurora.drive.core.common.annotation.qualifer.ThirdParties;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.AppScope;
import com.afterlogic.aurora.drive.core.common.interfaces.Configurable;

import dagger.Component;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * See {@link ApplicationAssemblyModule}.
 */
@AppScope
@Component(modules = ApplicationAssemblyModule.class)
public interface ApplicationAssemblyComponent {

    @ThirdParties
    Configurable thirdPartiesConfigurator();

    @Application
    Configurable applicationConfigurator();

    CoreAssemblyComponent plus(CoreAssemblyModule module);
}
