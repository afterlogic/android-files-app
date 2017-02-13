package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.main.interactor.MainFileListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.interactor.MainFileListInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.presenter.MainFileListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.main.presenter.MainFileListPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.router.MainFileListRouter;
import com.afterlogic.aurora.drive.presentation.modules.main.router.MainFileListRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListView;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFileListBiModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesListModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class MainFileListModule extends PresentationModule<MainFileListView> {

    @Provides @ModuleScope
    MainFileListViewModel viewModel(MainFileListBiModel model){
        return model;
    }

    @Provides @ModuleScope
    MainFilesListModel model(MainFileListBiModel model){
        return model;
    }

    @Provides @ModuleScope
    OptWeakRef<MainFileListPresenter> weakPresenter(){
        return OptWeakRef.empty();
    }

    @Provides @ModuleScope
    MainFileListPresenter presenter(MainFileListPresenterImpl presenter, OptWeakRef<MainFileListPresenter> weakRef){
        weakRef.set(presenter);
        return presenter;
    }

    @Provides
    MainFileListInteractor interactor(MainFileListInteractorImpl interactor){
        return interactor;
    }

    @Provides
    MainFileListRouter router(MainFileListRouterImpl router){
        return router;
    }
}
