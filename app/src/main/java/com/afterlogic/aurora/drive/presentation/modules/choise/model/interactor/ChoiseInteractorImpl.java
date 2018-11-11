package com.afterlogic.aurora.drive.presentation.modules.choise.model.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.interactor.BaseFilesInteractor;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseInteractorImpl extends BaseFilesInteractor implements ChoiseInteractor {

    @Inject
    ChoiseInteractorImpl(ObservableScheduler scheduler, FilesRepository filesRepository,
                         SessionManager sessionManager) {
        super(scheduler, filesRepository, sessionManager);
    }

}
