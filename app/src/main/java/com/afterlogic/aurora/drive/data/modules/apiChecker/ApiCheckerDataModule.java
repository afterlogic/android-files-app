package com.afterlogic.aurora.drive.data.modules.apiChecker;

import com.afterlogic.aurora.drive.data.modules.apiChecker.checker.ApiChecker;
import com.afterlogic.aurora.drive.data.modules.apiChecker.checker.ApiCheckerImpl;
import com.afterlogic.aurora.drive.data.modules.apiChecker.p7.P7ApiCheckerDataModule;
import com.afterlogic.aurora.drive.data.modules.apiChecker.p8.P8ApiCheckerDataModule;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module(includes = {
        P7ApiCheckerDataModule.class,
        P8ApiCheckerDataModule.class
})
public class ApiCheckerDataModule{

    @Provides
    ApiChecker apiChecker(ApiCheckerImpl apiChecker){
        return apiChecker;
    }
}
