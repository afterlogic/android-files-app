package com.afterlogic.aurora.drive.presentation.modules.choise.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.BaseFileRouter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.FilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.choise.model.interactor.ChoiseInteractor;
import com.afterlogic.aurora.drive.presentation.modules.choise.model.interactor.ChoiseInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.choise.model.presenter.ChoisePresenter;
import com.afterlogic.aurora.drive.presentation.modules.choise.model.presenter.ChoisePresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseView;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseBiModel;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseModel;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class ChoiseModule extends PresentationModule<ChoiseView>{

    @Provides
    ChoiseInteractor interactor(ChoiseInteractorImpl interactor){
        return interactor;
    }

    @Provides @ModuleScope
    OptWeakRef<ChoisePresenter> weakPresenter(){
        return OptWeakRef.empty();
    }

    @Provides @ModuleScope
    ChoisePresenter presenter(ChoisePresenterImpl presenter, OptWeakRef<ChoisePresenter> weak){
        weak.set(presenter);
        return presenter;
    }

    @Provides
    ChoiseViewModel viewModel(ChoiseBiModel model){
        return model;
    }

    @Provides
    ChoiseModel model(ChoiseBiModel model){
        return model;
    }

    @Provides
    FilesRouter router(BaseFileRouter<ChoiseView> router){
        return router;
    }
}
