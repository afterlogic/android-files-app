package com.afterlogic.aurora.drive.data.modules.auth.p8;

import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.data.modules.auth.AuthRepository;
import com.afterlogic.aurora.drive.data.modules.auth.repository.AuthRepositoryP8Impl;
import com.afterlogic.aurora.drive.data.modules.auth.p8.service.AuthServiceP8;
import com.afterlogic.aurora.drive.data.modules.auth.p8.service.AuthServiceP8Impl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class P8AuthDataModule {

    @Provides
    AuthServiceP8 provideAuthService(AuthServiceP8Impl authServiceP8){
        MyLog.d(this, "Provide auth service: " + authServiceP8);
        return authServiceP8;
    }

    @Provides @P8
    AuthRepository provideUserRepository(AuthRepositoryP8Impl userRepositoryP8){
        MyLog.d(this, "Provide user repository: " + userRepositoryP8);
        return userRepositoryP8;
    }
}
