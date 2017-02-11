package com.afterlogic.aurora.drive.presentation.modules.upload.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.BaseFilesInteractor;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadInteractorImpl extends BaseFilesInteractor implements UploadInteractor {

    @Inject UploadInteractorImpl(ObservableScheduler scheduler, FilesRepository filesRepository, AppResources appResources) {
        super(scheduler, filesRepository, appResources);
    }
}
