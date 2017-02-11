package com.afterlogic.aurora.drive.presentation.modules.upload.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.BaseFilesListInteractor;

import javax.inject.Inject;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFilesInteractorImpl extends BaseFilesListInteractor implements UploadFilesInteractor {
    @Inject UploadFilesInteractorImpl(ObservableScheduler scheduler, FilesRepository filesRepository) {
        super(scheduler, filesRepository);
    }
}
