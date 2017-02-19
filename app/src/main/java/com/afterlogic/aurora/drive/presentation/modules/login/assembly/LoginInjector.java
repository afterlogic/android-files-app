package com.afterlogic.aurora.drive.presentation.modules.login.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginView;

import javax.inject.Inject;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginInjector extends OldBaseInjector<LoginActivity, LoginView, LoginModule> implements Injector<LoginActivity>{

    @Inject
    LoginInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected LoginModule createModule() {
        return new LoginModule();
    }
}
