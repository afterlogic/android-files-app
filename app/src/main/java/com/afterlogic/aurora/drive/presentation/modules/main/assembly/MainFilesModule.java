package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMModule;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFilesInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFilesRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesView;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesViewModelImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class MainFilesModule extends MVVMModule<MainFilesActivity> {

    @Provides @ModuleScope
    MainFilesViewModel viewModel(MainFilesViewModelImpl model){
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
