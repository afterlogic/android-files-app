package com.afterlogic.aurora.drive.presentation.modules.start.assembly;


import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.start.interactor.StartInteractor;
import com.afterlogic.aurora.drive.presentation.modules.start.interactor.StartInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.start.presenter.StartPresenter;
import com.afterlogic.aurora.drive.presentation.modules.start.presenter.StartPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.start.router.StartRouter;
import com.afterlogic.aurora.drive.presentation.modules.start.router.StartRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class StartModule extends PresentationModule<StartView> {

    @Provides
    StartInteractor interactor(StartInteractorImpl interactor){
        return interactor;
    }

    @Provides
    StartRouter router(StartRouterImpl router){
        return router;
    }

    @Provides
    StartPresenter presenter(StartPresenterImpl presenter){
        return presenter;
    }
}
