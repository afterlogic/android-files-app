package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.SearchableFilesRootViewModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.offline.interactor.OfflineInteractor;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineViewModel extends SearchableFilesRootViewModel<OfflineFileListViewModel> {

    private final Router router;

    @Inject
    OfflineViewModel(OfflineInteractor interactor,
                               Subscriber subscriber,
                               Router router,
                               AppResources appResources,
                               ViewModelsConnection<OfflineFileListViewModel> viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
        this.router = router;
    }

    public void onOnlineModeClicked() {
        router.newRootScreen(AppRouter.MAIN);
    }
}
