package com.afterlogic.aurora.drive.presentation.modules.choise.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesFragment;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesView;

import dagger.Subcomponent;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = ChoiseFilesModule.class)
public interface ChoiseFilesComponent extends PresentationComponent<ChoiseFilesView, ChoiseFilesFragment> {
}
