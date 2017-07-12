package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import android.content.Context;

import com.afterlogic.aurora.drive.core.common.util.AppUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.FilesRootInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainInteractor extends FilesRootInteractor {

    private final AuthRepository authRepository;
    private final Context appContext;

    @Inject
    MainInteractor(FilesRepository filesRepository,
                             AppResources appResources,
                             AuthRepository authRepository,
                             Context appContext) {
        super(filesRepository, appResources);
        this.authRepository = authRepository;
        this.appContext = appContext;
    }

    public Completable logout() {
        return authRepository.logoutAndClearData()
                .andThen(Completable.fromAction(() -> {
                    appContext.stopService(FileObserverService.intent(appContext));
                    AppUtil.setComponentEnabled(FileObserverService.class, false, appContext);
                }));
    }

    public Single<String> getUserLogin() {
        return authRepository.getCurrentSession()
                .map(AuroraSession::getLogin)
                .toSingle();
    }
}
