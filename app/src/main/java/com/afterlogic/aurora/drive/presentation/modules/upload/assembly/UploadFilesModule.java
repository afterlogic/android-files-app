package com.afterlogic.aurora.drive.presentation.modules.upload.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.upload.interactor.UploadFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.upload.interactor.UploadFilesInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.upload.presenter.UploadFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.presenter.UploadFilesPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.upload.router.UploadFilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.upload.router.UploadFilesRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesView;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFilesBiModel;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFilesModel;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFilesViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class UploadFilesModule extends PresentationModule<UploadFilesView> {

    @Provides
    UploadFilesInteractor interactor(UploadFilesInteractorImpl interactor){
        return interactor;
    }

    @Provides @ModuleScope
    OptWeakRef<UploadFilesPresenter> weakPresenter(){
        return OptWeakRef.empty();
    }

    @Provides @ModuleScope
    UploadFilesPresenter presenter(UploadFilesPresenterImpl presenter, OptWeakRef<UploadFilesPresenter> weakRef){
        weakRef.set(presenter);
        return presenter;
    }

    @Provides
    UploadFilesViewModel viewModel(UploadFilesBiModel model){
        return model;
    }

    @Provides
    UploadFilesModel model(UploadFilesBiModel model){
        return model;
    }

    @Provides
    UploadFilesRouter router(UploadFilesRouterImpl router){
        return router;
    }
}
