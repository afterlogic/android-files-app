package com.afterlogic.aurora.drive.presentation.assembly.presentation;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.PresentationScope;
import com.afterlogic.aurora.drive.presentation.assembly.MVVMComponentsStore;
import com.afterlogic.aurora.drive.presentation.assembly.MVVMComponentsStoreImpl;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * See {@link PresentationAssemblyComponent}.
 */
@Module
public class PresentationAssemblyModule {

    public PresentationAssemblyModule() {

    }


    @Provides @PresentationScope
    PresentationModulesStore provideStore(){
        return new PresentationModulesStore();
    }

    @Provides @PresentationScope
    MVVMComponentsStore mvvmStore(MVVMComponentsStoreImpl store){
        return store;
    }
}
