package com.afterlogic.aurora.drive.presentation.modules.main.interactor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.afterlogic.aurora.drive.core.common.util.AppUtil;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.auth.AuthenticatorService;
import com.afterlogic.aurora.drive.data.modules.cleaner.DataCleaner;
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

    private final AuthenticatorService authenticatorService;
    private final DataCleaner dataCleaner;
    private final SessionManager sessionManager;
    private final Context appContext;

    @Inject
    MainInteractor(FilesRepository filesRepository,
                   AuthenticatorService authenticatorService,
                   DataCleaner dataCleaner,
                   SessionManager sessionManager,
                   Context appContext) {
        super(filesRepository);
        this.authenticatorService = authenticatorService;
        this.dataCleaner = dataCleaner;
        this.sessionManager = sessionManager;
        this.appContext = appContext;
    }

    public boolean isP8() {

        AuroraSession session = sessionManager.getSession();
        return session != null && session.getApiVersion() == Const.ApiVersion.API_P8;

    }

    public Completable logout() {
        return authenticatorService.logout()
                .andThen(dataCleaner.cleanAllUserData())
                .andThen(Completable.fromAction(() -> {
                    appContext.stopService(FileObserverService.intent(appContext));
                    AppUtil.setComponentEnabled(FileObserverService.class, false, appContext);
                }));
    }

    public Single<String> getUserLogin() {

        return Single.fromCallable(() -> {

            AuroraSession session = sessionManager.getSession();

            if (session == null) {
                throw new IllegalStateException("Not authorized.");
            }

            return session.getUser();

        });

    }

    public Single<Boolean> getNetworkState() {

        return Single.fromCallable(() -> {

            ConnectivityManager cm =
                    (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm == null) return false;

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        });

    }
}
