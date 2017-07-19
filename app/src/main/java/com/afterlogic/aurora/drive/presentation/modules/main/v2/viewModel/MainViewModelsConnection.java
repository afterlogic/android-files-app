package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
class MainViewModelsConnection extends ViewModelsConnection<MainFilesListViewModel> {

    PublishSubject<AuroraFile> fileClickedPublisher = PublishSubject.create();

    @Inject
    MainViewModelsConnection() { }

}
