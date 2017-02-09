package com.afterlogic.aurora.drive.presentation.modules.filelist.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.filelist.interactor.FileListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.filelist.interactor.FileListInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.filelist.presenter.FileListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.presenter.FileListPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.filelist.router.FileListRouter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.router.FileListRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListView;
import com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel.FileListModel;
import com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel.FileListViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class FileListModule extends PresentationModule<FileListView> {

    @Provides @ModuleScope
    FileListViewModel viewModel(AppResources resources){
        return new FileListViewModel(resources);
    }

    @Provides
    FileListModel model(FileListViewModel viewModel){
        return viewModel.getModel();
    }

    @Provides @ModuleScope
    FileListPresenter presenter(FileListPresenterImpl presenter){
        return presenter;
    }

    @Provides
    FileListInteractor interactor(FileListInteractorImpl interactor){
        return interactor;
    }

    @Provides
    FileListRouter router(FileListRouterImpl router){
        return router;
    }
}
