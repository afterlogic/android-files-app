package com.afterlogic.aurora.drive.presentation.modules.login.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.login.model.interactor.LoginInteractor;
import com.afterlogic.aurora.drive.presentation.modules.login.model.interactor.LoginInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.login.model.presenter.LoginPresenter;
import com.afterlogic.aurora.drive.presentation.modules.login.model.presenter.LoginPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.login.model.router.LoginRouter;
import com.afterlogic.aurora.drive.presentation.modules.login.model.router.LoginRouterImpl;
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

    @Provides @ModuleScope
    LoginViewModel viewModel(AppResources resources){
        return new LoginViewModel(resources);
    }

    @Provides
    LoginModel model(LoginViewModel model){
        return model.getController();
    }

    @Provides @ModuleScope
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
