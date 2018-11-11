package com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFilesListInteractor extends BaseInteractor implements FilesListInteractor {

    private final FilesRepository mFilesRepository;

    @Inject
    public BaseFilesListInteractor(ObservableScheduler scheduler,
                            FilesRepository filesRepository) {
        super(scheduler);
        mFilesRepository = filesRepository;
    }

    @Override
    public Single<List<AuroraFile>> getFilesList(AuroraFile folder) {
        return mFilesRepository.getFiles(folder)
                .compose(this::composeDefault);
    }

    @Override
    public Single<Uri> getThumbnail(AuroraFile file) {
        return mFilesRepository.getFileThumbnail(file)
                .compose(this::composeDefault);
    }

}
