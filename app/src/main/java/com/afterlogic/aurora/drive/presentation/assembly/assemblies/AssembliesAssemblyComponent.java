package com.afterlogic.aurora.drive.presentation.assembly.assemblies;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Presentation's dagger modules component creator.
 *
 * NOTE:
 * For default component creation method in BaseWireframe all methods must be called 'plus'.
 */
@Subcomponent(modules = AssembliesAssemblyModule.class)
public interface AssembliesAssemblyComponent {

    PresentationModulesStore store();
}
