package com.afterlogic.aurora.drive.presentation.modules.login.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMModule;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.LoginInteractor;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.LoginInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.login.router.LoginRouter;
import com.afterlogic.aurora.drive.presentation.modules.login.router.LoginRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModel;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModelImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class LoginModule extends MVVMModule<LoginActivity> {

    @Provides @ModuleScope
    LoginViewModel viewModel(LoginViewModelImpl viewModel){
        return viewModel;
    }

    @Provides
    LoginRouter router(LoginRouterImpl router){
        return router;
    }

    @Provides
    LoginInteractor interactor(LoginInteractorImpl interactor){
        return interactor;
    }
}
