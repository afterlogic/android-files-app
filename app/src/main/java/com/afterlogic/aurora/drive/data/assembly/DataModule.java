package com.afterlogic.aurora.drive.data.assembly;


import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;
import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStoreImpl;
import com.afterlogic.aurora.drive.data.common.db.DataBaseModule;
import com.afterlogic.aurora.drive.data.common.network.DynamicDomainProvider;
import com.afterlogic.aurora.drive.data.common.network.NetworkDataModule;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.cleaner.DataCleanerModule;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefs;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefsImpl;
import com.afterlogic.aurora.drive.model.AuroraSession;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Will provide repositories, services and etc.
 */
@Module(includes = {
        DataBaseModule.class,
        NetworkDataModule.class,
        FilesDataModule.class,
        DataCleanerModule.class
})
public class DataModule {

    @Provides @DataScope
    DynamicDomainProvider provideDynamicLink(SessionManager sessionManager){
        return () -> {
            AuroraSession session = sessionManager.getSession();

            if (session == null) {
                throw new IllegalStateException(
                        "Not authorized. So dynamic domain can't be provided."
                );
            }

            return sessionManager.getSession().getDomain();
        };
    }

    @Provides @DataScope
    AppPrefs provideAppPrefs(AppPrefsImpl prefsData){
        return prefsData;
    }


    @Provides @DataScope
    SharedObservableStore sharedObservableStore(SharedObservableStoreImpl sharedObservableStore){
        return sharedObservableStore;
    }

    //TODO remove qualifier @RepositoryCache
    @Provides @RepositoryCache @DataScope
    SharedObservableStore repositoryCache(SharedObservableStore sharedObservableStore){
        return sharedObservableStore;
    }
}
