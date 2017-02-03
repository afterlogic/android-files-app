package com.afterlogic.aurora.drive.presentation.modules.login.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.qualifer.Internal;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.PresentationScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.LoginInteractor;
import com.afterlogic.aurora.drive.presentation.modules.login.interactor.LoginInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.login.presenter.LoginPresenter;
import com.afterlogic.aurora.drive.presentation.modules.login.presenter.LoginPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.login.router.LoginRouter;
import com.afterlogic.aurora.drive.presentation.modules.login.router.LoginRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginView;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginModel;
import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class LoginModule extends PresentationModule<LoginView>{

    @Provides @PresentationScope
    LoginViewModel viewModel(@Internal LoginViewModel viewModel){
        return viewModel;
    }

    @Provides
    LoginModel model(LoginViewModel model){
        return model.getController();
    }


    @Provides @PresentationScope
    LoginPresenter presenter(LoginPresenterImpl presenter){
        return presenter;
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
