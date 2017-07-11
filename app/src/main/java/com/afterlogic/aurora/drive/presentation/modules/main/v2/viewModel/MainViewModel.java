package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.BaseFilesRootInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.BaseFilesRootViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainViewModel extends BaseFilesRootViewModel<MainFilesListViewModel> {

    @Inject
    protected MainViewModel(BaseFilesRootInteractor interactor,
                            Subscriber subscriber,
                            Router router,
                            AppResources appResources,
                            ViewModelsConnection<MainFilesListViewModel> viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
    }
}
