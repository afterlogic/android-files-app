package com.afterlogic.aurora.drive.presentation.assembly.wireframes;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Presentation module's wireframes factory module.
 */
@Module
public class WireframeFactoryModule {

    private AssembliesAssemblyComponent mPresentationAssemblyComponent;

    public WireframeFactoryModule(AssembliesAssemblyComponent presentationAssemblyComponent) {
        mPresentationAssemblyComponent = presentationAssemblyComponent;
    }

    /**
     * Provide {@link AssembliesAssemblyComponent} for creating and accessing to module's assembly
     * component when create wireframe for it.
     * @return - app configured {@link AssembliesAssemblyComponent}.
     */
    @Provides
    AssembliesAssemblyComponent provideAssemblies(){
        return mPresentationAssemblyComponent;
    }

}
