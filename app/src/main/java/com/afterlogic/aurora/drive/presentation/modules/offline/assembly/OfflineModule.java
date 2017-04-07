package com.afterlogic.aurora.drive.presentation.modules.offline.assembly;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMModule;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.interactor.OfflineInteractor;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.interactor.OfflineInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineViewModelImpl;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.presenter.OfflineModelOutput;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.presenter.OfflinePresenter;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.presenter.OfflinePresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.router.OfflineRouter;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.router.OfflineRouterImpl;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class OfflineModule extends MVVMModule<OfflineActivity> {

    @Provides
    OfflineViewModel viewModel(OfflineViewModelImpl model){
        return model;
    }

    @Provides
    OfflineModelOutput model(OfflineViewModelImpl model){
        return model.getModel();
    }

    @Provides
    OfflinePresenter presenter(OfflinePresenterImpl presenter){
        return presenter;
    }

    @Provides
    OfflineInteractor interactor(OfflineInteractorImpl interactor){
        return interactor;
    }

    @Provides
    OfflineRouter router(OfflineRouterImpl router){
        return router;
    }
}
