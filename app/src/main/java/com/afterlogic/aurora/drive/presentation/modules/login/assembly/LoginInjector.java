package com.afterlogic.aurora.drive.presentation.modules.login.assembly;


import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;

import javax.inject.Inject;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginInjector implements Injector<LoginActivity>{

    private final ModulesComponentCreator mCreator;

    @Inject
    LoginInjector(ModulesComponentCreator component) {
        mCreator = component;
    }

    @Override
    public void inject(LoginActivity target) {
        LoginComponent component = mCreator.login();
        component.module().setView(target);
        component.inject(target);
    }
}
