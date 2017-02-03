package com.afterlogic.aurora.drive._unrefactored.data.assembly;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.afterlogic.aurora.drive._unrefactored.core.annotations.qualifers.Project7;
import com.afterlogic.aurora.drive._unrefactored.core.annotations.qualifers.Project8;
import com.afterlogic.aurora.drive._unrefactored.core.annotations.qualifers.RepositoryCache;
import com.afterlogic.aurora.drive._unrefactored.core.annotations.scoupes.AppScoupe;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStoreImpl;
import com.afterlogic.aurora.drive.data.modules.apiChecker.checker.ApiChecker;
import com.afterlogic.aurora.drive.data.modules.apiChecker.checker.ApiCheckerImpl;
import com.afterlogic.aurora.drive._unrefactored.data.common.ApiConfigurator;
import com.afterlogic.aurora.drive._unrefactored.data.common.DynamicDomainProvider;
import com.afterlogic.aurora.drive._unrefactored.data.common.SessionManager;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class DataModule {

    private Context mContext;

    private ApiConfigurator mApiConfigurator = new ApiConfigurator();
    private SessionManager mSessionManager = new SessionManager(mApiConfigurator);

    public DataModule(Context context) {
        mContext = context;
    }

    @Provides
    Context provideApplicationContext(){
        return mContext;
    }

    @Provides @AppScoupe
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

    @Provides @AppScoupe @RepositoryCache
    SharedObservableStore provideRepositoryCache(SharedObservableStoreImpl cache){
        return cache;
    }

    @Provides
    DynamicDomainProvider provideDynamicLink(ApiConfigurator configurator){
        return configurator;
    }

    @Provides
    AuthRepository provideUserRepository(@Project7 Provider<AuthRepository> p7,
                                         @Project8 Provider<AuthRepository> p8,
                                         ApiConfigurator configurator){
        return chooseByApiVersion(configurator, p7, p8);
    }

    @Provides
    FilesRepository provideFilesRepository(@Project7 Provider<FilesRepository> p7,
                                           @Project8 Provider<FilesRepository> p8,
                                           ApiConfigurator configurator){
        return chooseByApiVersion(configurator, p7, p8);
    }

    @Provides
    ApiChecker provideApiChecker(ApiCheckerImpl apiChecker){
        return apiChecker;
    }

    private  <T> T chooseByApiVersion(ApiConfigurator configurator, Provider<T> p7, Provider<T> p8){
        switch (configurator.getCurrentApiVersion()){
            case Const.ApiVersion.API_P7:
                return p7.get();
            case Const.ApiVersion.API_P8:
                return p8.get();
            default:
                return null;
        }
    }

}
