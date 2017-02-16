package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncView;

import dagger.Subcomponent;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = SyncModule.class)
public interface SyncComponent extends PresentationComponent<SyncView, SyncService> {
}
