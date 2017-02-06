package com.afterlogic.aurora.drive.data.modules.auth;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.ApiConfigurator;
import com.afterlogic.aurora.drive.data.common.util.MultiApiUtil;
import com.afterlogic.aurora.drive.data.modules.auth.p7.P7AuthDataModule;
import com.afterlogic.aurora.drive.data.modules.auth.p8.P8AuthDataModule;

import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module(includes = {
        P7AuthDataModule.class,
        P8AuthDataModule.class
})
public class AuthDataModule{

    @Provides
    AuthRepository repository(ApiConfigurator configurator, @P7 Provider<AuthRepository> p7, @P8 Provider<AuthRepository> p8){
        return MultiApiUtil.chooseByApiVersion(configurator, p7, p8);
    }
}
