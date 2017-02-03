package com.afterlogic.aurora.drive.presentation.modules.filesMain.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesView;

import dagger.Subcomponent;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = MainFilesModule.class)
public interface MainFilesComponent extends PresentationComponent<MainFilesView, MainFilesActivity> {
}
