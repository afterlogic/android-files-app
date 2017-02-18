package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListView;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListFragment;

import dagger.Subcomponent;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = MainFileListModule.class)
public interface MainFileListComponent extends PresentationComponent<MainFileListView, MainFileListFragment> {
}
