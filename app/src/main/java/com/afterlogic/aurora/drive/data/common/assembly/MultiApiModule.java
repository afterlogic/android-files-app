package com.afterlogic.aurora.drive.data.common.assembly;

import com.afterlogic.aurora.drive.data.common.network.ApiConfigurator;
import com.afterlogic.aurora.drive.core.consts.Const;

import javax.inject.Provider;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MultiApiModule {

    private final ApiConfigurator mApiConfigurator;

    public MultiApiModule(ApiConfigurator apiConfigurator) {
        mApiConfigurator = apiConfigurator;
    }

    public <T> T chooseByApiVersion(Provider<T> p7, Provider<T> p8){
        switch (mApiConfigurator.getCurrentApiVersion()){
            case Const.ApiVersion.API_P7:
                return p7.get();
            case Const.ApiVersion.API_P8:
                return p8.get();
            default:
                return null;
        }
    }
}
