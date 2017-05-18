package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesActivity;

import dagger.Subcomponent;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = MainFilesModule.class)
public interface MainFilesComponent extends MVVMComponent<MainFilesActivity> {
}
