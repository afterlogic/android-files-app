package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.BaseInjector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncView;

import javax.inject.Inject;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SyncInjector extends BaseInjector<SyncService, SyncView, SyncModule> implements Injector<SyncService> {

    @Inject
    SyncInjector(AssembliesAssemblyComponent component) {
        super(component);
    }

    @NonNull
    @Override
    protected SyncModule createModule() {
        return new SyncModule();
    }
}
