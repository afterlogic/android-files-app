package com.afterlogic.aurora.drive.presentation.modules.main.model.interactor;

import android.content.Context;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.core.common.util.AppUtil;
import com.afterlogic.aurora.drive.data.common.db.DataBaseProvider;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.interactor.BaseFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesInteractorImpl extends BaseFilesInteractor implements MainFilesInteractor {

    private final AuthRepository mAuthRepository;
    private final FilesRepository mFilesRepository;
    private final DataBaseProvider mDataBaseProvider;
    private final Context mAppContext;

    @Inject MainFilesInteractorImpl(ObservableScheduler scheduler,
                                    FilesRepository filesRepository,
                                    AppResources appResources,
                                    AuthRepository authRepository, DataBaseProvider dataBaseProvider, Context appContext) {
        super(scheduler, filesRepository, appResources);
        mFilesRepository = filesRepository;
        mAuthRepository = authRepository;
        mDataBaseProvider = dataBaseProvider;
        mAppContext = appContext;
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
                .andThen(mFilesRepository.clearOfflineData())
                .andThen(Completable.fromAction(() -> {
                    mDataBaseProvider.reset();
                    mAppContext.stopService(FileObserverService.intent(mAppContext));
                    AppUtil.setComponentEnabled(FileObserverService.class, false, mAppContext);
                }))
                .compose(this::composeDefault);
    }
}
