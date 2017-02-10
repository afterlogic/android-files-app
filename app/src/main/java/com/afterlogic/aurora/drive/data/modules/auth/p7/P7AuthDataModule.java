package com.afterlogic.aurora.drive.data.modules.auth.p7;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.auth.repository.AuthRepositoryP7Impl;
import com.afterlogic.aurora.drive.data.modules.auth.p7.service.AuthServiceP7;
import com.afterlogic.aurora.drive.data.modules.auth.p7.service.AuthServiceP7Impl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class P7AuthDataModule {

    @Provides
    AuthServiceP7 provideAuthService(AuthServiceP7Impl authServiceP7){
        return authServiceP7;
    }

    @Provides @P7
    AuthRepository provideUserRepository(AuthRepositoryP7Impl userRepositoryP7){
        return userRepositoryP7;
    }
}
