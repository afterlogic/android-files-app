package com.afterlogic.aurora.drive.presentation.modules.choise.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseActivity;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseView;

import dagger.Subcomponent;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = ChoiseModule.class)
public interface ChoiseComponent extends PresentationComponent<ChoiseView, ChoiseActivity> {
}
