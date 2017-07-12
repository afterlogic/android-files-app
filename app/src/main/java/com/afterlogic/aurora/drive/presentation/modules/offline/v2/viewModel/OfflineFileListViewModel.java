package com.afterlogic.aurora.drive.presentation.modules.offline.v2.viewModel;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.offline.v2.interactor.OfflineFileListInteractor;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFileListViewModel extends SearchableFileListViewModel<OfflineFileListViewModel, OfflineFileViewModel, OfflineArgs> {

    private final FileMapper mapper;

    @Inject
    protected OfflineFileListViewModel(OfflineFileListInteractor interactor,
                                       Subscriber subscriber,
                                       ViewModelsConnection<OfflineFileListViewModel> viewModelsConnection,
                                       FileMapper mapper) {
        super(interactor, subscriber, viewModelsConnection);
        this.mapper = mapper;
    }

    @Override
    protected OfflineFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return mapper.map(file, onItemClickListener);
    }
}
