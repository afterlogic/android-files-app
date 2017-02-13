package com.afterlogic.aurora.drive.presentation.modules.choise.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.choise.interactor.ChoiseFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.choise.interactor.ChoiseFilesInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.choise.presenter.ChoiseFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.choise.presenter.ChoiseFilesPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.choise.router.ChoiseFilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.choise.router.ChoiseFilesRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesView;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseFilesBiModel;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseFilesModel;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseFilesViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class ChoiseFilesModule extends PresentationModule<ChoiseFilesView> {

    @Provides
    ChoiseFilesInteractor interactor(ChoiseFilesInteractorImpl interactor){
        return interactor;
    }

    @Provides @ModuleScope
    OptWeakRef<ChoiseFilesPresenter> weakPresenter(){
        return OptWeakRef.empty();
    }

    @Provides @ModuleScope
    ChoiseFilesPresenter presenter(ChoiseFilesPresenterImpl presenter, OptWeakRef<ChoiseFilesPresenter> weakRef){
        weakRef.set(presenter);
        return presenter;
    }

    @Provides
    ChoiseFilesViewModel viewModel(ChoiseFilesBiModel model){
        return model;
    }

    @Provides
    ChoiseFilesModel model(ChoiseFilesBiModel model){
        return model;
    }

    @Provides
    ChoiseFilesRouter router(ChoiseFilesRouterImpl router){
        return router;
    }
}
