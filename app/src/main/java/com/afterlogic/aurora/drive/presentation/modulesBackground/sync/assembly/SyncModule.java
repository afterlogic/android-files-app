package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.assembly;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.interactor.SyncInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.interactor.SyncInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.presenter.SyncPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.presenter.SyncPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncView;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncBiModel;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncModel;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class SyncModule extends PresentationModule<SyncView>{

    @Provides
    SyncPresenter presenter(SyncPresenterImpl presenter){
        return presenter;
    }

    @Provides
    SyncInteractor interactor(SyncInteractorImpl interactor){
        return interactor;
    }

    @Provides
    SyncModel model(SyncBiModel model){
        return model;
    }

    @Provides
    SyncViewModel viewModel(SyncBiModel model){
        return model;
    }
}
