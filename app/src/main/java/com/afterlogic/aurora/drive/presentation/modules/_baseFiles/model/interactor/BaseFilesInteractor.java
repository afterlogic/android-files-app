package com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseFilesInteractor extends BaseInteractor implements FilesInteractor {

    private final FilesRepository mFilesRepository;
    private final SessionManager mSessionManager;

    @Inject
    public BaseFilesInteractor(ObservableScheduler scheduler,
                               FilesRepository filesRepository,
                               SessionManager sessionManager) {
        super(scheduler);
        mFilesRepository = filesRepository;
        mSessionManager = sessionManager;
    }

    @Override
    public Single<List<Storage>> getAvailableFileTypes() {
        return mFilesRepository.getAvailableStorages()
                .compose(this::composeDefault);
    }

    @Override
    public Single<Boolean> getAuthStatus() {
        return Single.fromCallable(() -> mSessionManager.getSession() != null);
    }
}
