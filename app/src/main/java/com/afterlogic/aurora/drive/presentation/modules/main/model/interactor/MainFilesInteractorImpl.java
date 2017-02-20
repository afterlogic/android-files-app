package com.afterlogic.aurora.drive.presentation.modules.main.model.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.interactor.BaseFilesInteractor;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesInteractorImpl extends BaseFilesInteractor implements MainFilesInteractor {

    private final AuthRepository mAuthRepository;

    @Inject MainFilesInteractorImpl(ObservableScheduler scheduler,
                                    FilesRepository filesRepository,
                                    AppResources appResources,
                                    AuthRepository authRepository) {
        super(scheduler, filesRepository, appResources);
        mAuthRepository = authRepository;
    }

    @Override
    public Single<String> getUserLogin() {
        return mAuthRepository.getCurrentSession()
                .map(AuroraSession::getLogin)
                .toSingle()
                .compose(this::composeDefault);
    }

    @Override
    public Completable logout() {
        return mAuthRepository.logoutAndClearData()
                .compose(this::composeDefault);
    }
}
