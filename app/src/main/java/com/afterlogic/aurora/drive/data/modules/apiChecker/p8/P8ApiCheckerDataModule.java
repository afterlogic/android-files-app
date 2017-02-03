package com.afterlogic.aurora.drive.data.modules.apiChecker.p8;

import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.modules.apiChecker.ApiCheckRepository;
import com.afterlogic.aurora.drive.data.modules.apiChecker.p8.repository.ApiCheckRepository8Impl;
import com.afterlogic.aurora.drive.data.modules.apiChecker.p8.service.ApiCheckerServiceP8;
import com.afterlogic.aurora.drive.data.modules.apiChecker.p8.service.ApiCheckerServiceP8Impl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class P8ApiCheckerDataModule {

    @Provides
    @P8
    ApiCheckRepository provideCheckRepository(ApiCheckRepository8Impl repository8){
        return repository8;
    }

    @Provides
    ApiCheckerServiceP8 provideCheckerService(ApiCheckerServiceP8Impl serviceP8){
        return serviceP8;
    }
}
