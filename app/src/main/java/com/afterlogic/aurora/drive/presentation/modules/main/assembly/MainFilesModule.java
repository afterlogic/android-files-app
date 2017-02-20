package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFilesInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.model.presenter.MainFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.main.model.presenter.MainFilesPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFilesRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesView;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesBiModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFilesModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class MainFilesModule extends PresentationModule<MainFilesView> {

    @Provides @ModuleScope
    OptWeakRef<MainFilesPresenter> weakPresenter(){
        return OptWeakRef.empty();
    }

    @Provides @ModuleScope
    MainFilesPresenter presenter(MainFilesPresenterImpl presenter, OptWeakRef<MainFilesPresenter> weakPresenter){
        weakPresenter.set(presenter);
        return presenter;
    }

    @Provides @ModuleScope
    MainFilesViewModel viewModel(MainFilesBiModel model){
        return model;
    }

    @Provides @ModuleScope
    MainFilesModel model(MainFilesBiModel model){
        return model;
    }

    @Provides
    MainFilesInteractor interactor(MainFilesInteractorImpl interactor){
        return interactor;
    }

    @Provides
    MainFilesRouter router(MainFilesRouterImpl router){
        return router;
    }

}
