package com.afterlogic.aurora.drive.presentation.assembly.presentation;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.PresentationScope;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyModule;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryModule;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Root presentation component. Create child presentation's modules factory component
 * and presentation module's dagger modules component.
 */
@PresentationScope
@Subcomponent(modules = PresentationAssemblyModule.class)
public interface PresentationAssemblyComponent {
    AssembliesAssemblyComponent plus(AssembliesAssemblyModule module);
    ModulesFactoryComponent plus(ModulesFactoryModule module);
}
