package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.offline.interactor.OfflineFileListInteractor;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFileListViewModel extends SearchableFileListViewModel<OfflineFileListViewModel, OfflineFileViewModel, OfflineArgs> {

    public final ObservableField<OfflineHeader> header = new ObservableField<>();

    private final FileMapper mapper;

    @Inject
    OfflineFileListViewModel(OfflineFileListInteractor interactor,
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

    @Override
    public void setArgs(OfflineArgs args) {
        super.setArgs(args);
        header.set(new OfflineHeader(args.isManual()));
    }
}
