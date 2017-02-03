package com.afterlogic.aurora.drive.data.modules.apiChecker.p7;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.modules.apiChecker.ApiCheckRepository;
import com.afterlogic.aurora.drive.data.modules.apiChecker.p7.repository.ApiCheckRepository7Impl;
import com.afterlogic.aurora.drive.data.modules.apiChecker.p7.service.ApiCheckerServiceP7;
import com.afterlogic.aurora.drive.data.modules.apiChecker.p7.service.ApiCheckerServiceP7Impl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class P7ApiCheckerDataModule {

    @Provides
    @P7
    ApiCheckRepository provideApiCheckRepository(ApiCheckRepository7Impl repository7){
        return repository7;
    }

    @Provides
    ApiCheckerServiceP7 provideApiCheckerService(ApiCheckerServiceP7Impl serviceP7){
        return serviceP7;
    }
}
