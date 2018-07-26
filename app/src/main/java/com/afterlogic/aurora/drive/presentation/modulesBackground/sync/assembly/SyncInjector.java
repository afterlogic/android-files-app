package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.assembly;

import androidx.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncView;

import javax.inject.Inject;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SyncInjector extends OldBaseInjector<SyncService, SyncView, SyncModule> implements Injector<SyncService> {

    @Inject
    SyncInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected SyncModule createModule() {
        return new SyncModule();
    }
}
