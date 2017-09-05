package com.afterlogic.aurora.drive.data.modules.cleaner;

import android.webkit.CookieManager;

import com.afterlogic.aurora.drive.core.common.contextWrappers.account.AccountHelper;
import com.afterlogic.aurora.drive.data.common.db.DataBaseProvider;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesLocalService;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by sashka on 21.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class DataCleanerImpl implements DataCleaner {

    private final FilesLocalService mFilesLocalService;
    private final SessionManager mSessionManager;
    private final DataBaseProvider mDataBaseProvider;
    private final AccountHelper accountHelper;

    @Inject
    DataCleanerImpl(FilesLocalService filesLocalService,
                    SessionManager sessionManager,
                    DataBaseProvider dataBaseProvider,
                    AccountHelper accountHelper) {
        mFilesLocalService = filesLocalService;
        mSessionManager = sessionManager;
        mDataBaseProvider = dataBaseProvider;
        this.accountHelper = accountHelper;
    }

    @Override
    public Completable cleanAllUserData(){
        return cleanUserAccountAndSession()
                .andThen(mFilesLocalService.clear())
                .andThen(Completable.fromAction(mDataBaseProvider::reset))
                .andThen(Completable.fromAction(() -> CookieManager.getInstance().removeAllCookie()));
    }

    @Override
    public Completable cleanUserAccountAndSession(){
        return Completable.fromAction(() -> {
            accountHelper.removeCurrentAccount();
            mSessionManager.notifySessionChanged();
        });
    }
}
