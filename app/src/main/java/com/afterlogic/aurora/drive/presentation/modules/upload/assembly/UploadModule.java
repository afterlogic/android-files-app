package com.afterlogic.aurora.drive.presentation.modules.upload.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.BaseFileRouter;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.router.FilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.upload.model.interactor.UploadInteractor;
import com.afterlogic.aurora.drive.presentation.modules.upload.model.interactor.UploadInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.upload.model.presenter.UploadPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.model.presenter.UploadPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadView;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadBiModel;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadModel;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class UploadModule extends PresentationModule<UploadView>{

    @Provides
    UploadInteractor interactor(UploadInteractorImpl interactor){
        return interactor;
    }

    @Provides @ModuleScope
    OptWeakRef<UploadPresenter> weakPresenter(){
        return OptWeakRef.empty();
    }

    @Provides @ModuleScope
    UploadPresenter presenter(UploadPresenterImpl presenter, OptWeakRef<UploadPresenter> weak){
        weak.set(presenter);
        return presenter;
    }

    @Provides
    UploadViewModel viewModel(UploadBiModel model){
        return model;
    }

    @Provides
    UploadModel model(UploadBiModel model){
        return model;
    }

    @Provides
    FilesRouter router(BaseFileRouter<UploadView> router){
        return router;
    }
}
