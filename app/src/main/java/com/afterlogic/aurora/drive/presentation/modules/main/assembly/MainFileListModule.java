package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFileListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.main.model.interactor.MainFileListInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.model.presenter.MainFileListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.main.model.presenter.MainFileListPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFileListRouter;
import com.afterlogic.aurora.drive.presentation.modules.main.model.router.MainFileListRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListView;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFileListViewModelImpl;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.model.MainFilesListModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class MainFileListModule extends PresentationModule<MainFileListView> {

    @Provides @ModuleScope
    MainFileListViewModel viewModel(MainFileListViewModelImpl model){
        return model;
    }

    @Provides @ModuleScope
    MainFilesListModel model(MainFileListViewModelImpl model){
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
