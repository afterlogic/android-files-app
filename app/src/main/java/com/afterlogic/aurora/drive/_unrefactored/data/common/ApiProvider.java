package com.afterlogic.aurora.drive._unrefactored.data.common;

import com.afterlogic.aurora.drive._unrefactored.data.common.repository.FilesRepository;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.UserRepository;

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
    Provider<UserRepository> mUserRepositoryProvider;

    @Inject
    Provider<FilesRepository> mFilesRepositoryProvider;

    public SessionManager getSessionManager() {
        return mSessionManager;
    }

    public UserRepository getUserRepository() {
        return mUserRepositoryProvider.get();
    }

    public FilesRepository getFilesRepository(){
        return mFilesRepositoryProvider.get();
    }
}
