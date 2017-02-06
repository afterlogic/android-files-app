package com.afterlogic.aurora.drive._unrefactored.data.common;

import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiProvider {

    @Inject
    SessionManager mSessionManager;

    @Inject
    Provider<AuthRepository> mUserRepositoryProvider;

    @Inject
    Provider<FilesRepository> mFilesRepositoryProvider;

    public SessionManager getSessionManager() {
        return mSessionManager;
    }

    public AuthRepository getUserRepository() {
        return mUserRepositoryProvider.get();
    }

    public FilesRepository getFilesRepository(){
        return mFilesRepositoryProvider.get();
    }
}
