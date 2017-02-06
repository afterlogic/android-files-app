package com.afterlogic.aurora.drive.presentation.modules.filesMain.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.presentation.common.modules.interactor.BaseInteractor;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.FileType;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesInteractorImpl extends BaseInteractor implements MainFilesInteractor {

    private final FilesRepository mFilesRepository;

    @Inject MainFilesInteractorImpl(ObservableScheduler scheduler,
                                    FilesRepository filesRepository) {
        super(scheduler);
        mFilesRepository = filesRepository;
    }

    @Override
    public Single<List<FileType>> getAvailableFileTypes() {
        return mFilesRepository.getAvailableFileTypes()
                .map(types -> Stream.of(types)
                        .map(type -> new FileType(type, type))
                        .collect(Collectors.toList())
                )
                .compose(this::composeDefault);
    }
}
