package com.afterlogic.aurora.drive.presentation.modules.offline.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;

import dagger.Subcomponent;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = OfflineModule.class)
public interface OfflineComponent extends MVVMComponent<OfflineActivity>{
}
