package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.BaseFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.BaseFileListArgs;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.BaseFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListViewModel extends BaseFileListViewModel<
        MainFilesListViewModel, MainFileViewModel, BaseFileListArgs
> {

    @Inject
    protected MainFilesListViewModel(BaseFilesListInteractor interactor,
                                     Subscriber subscriber,
                                     ViewModelsConnection<MainFilesListViewModel> viewModelsConnection) {
        super(interactor, subscriber, viewModelsConnection);
    }

    @Override
    protected MainFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return new MainFileViewModel(file, onItemClickListener);
    }
}
