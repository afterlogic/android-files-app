package com.afterlogic.aurora.drive.presentation.modules.login.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginView;

import dagger.Subcomponent;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

@ModuleScope
@Subcomponent(modules = LoginModule.class)
public interface LoginComponent extends MVVMComponent<LoginActivity> {
}
