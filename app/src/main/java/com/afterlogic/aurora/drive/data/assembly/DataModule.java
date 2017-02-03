package com.afterlogic.aurora.drive.data.assembly;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.network.ApiConfigurator;
import com.afterlogic.aurora.drive.data.common.network.DynamicDomainProvider;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStoreImpl;
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
 * Will provideFor repositories, services and etc.
 */
@Module(includes = {
        FilesDataModule.class
})
public class DataModule {

    //TODO optimize
    private ApiConfigurator mApiConfigurator = new ApiConfigurator();
    private SessionManager mSessionManager = new SessionManager(mApiConfigurator);

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

    @Provides @DataScope
    SessionManager provideSessionManager(Context context){
        if (mSessionManager.getAuroraSession() == null) {
            //If current session loosed get it from account
            Account account = AccountUtil.getCurrentAccount(context);
            if (account != null) {
                AccountManager am = AccountManager.get(context);
                AuroraSession session = AccountUtil.fromAccount(account, am);
                mSessionManager.setAuroraSession(session);
            }
        }
        return mSessionManager;
    }

    @Provides
    ApiConfigurator provideApiConfigurator(){
        AuroraSession session = mSessionManager.getAuroraSession();
        if (session != null && session.getApiVersion() != Const.ApiVersion.API_NONE && mApiConfigurator.getCurrentApiVersion() == Const.ApiVersion.API_NONE){
            mApiConfigurator.setDomain(session.getDomain(), session.getApiVersion());
        }
        return mApiConfigurator;
    }

    @Provides
    DynamicDomainProvider provideDynamicLink(ApiConfigurator configurator){
        return configurator;
    }
}
