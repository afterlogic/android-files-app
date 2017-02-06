package com.afterlogic.aurora.drive.data.common.network;

import com.afterlogic.aurora.drive.data.common.network.p7.P7NetworkDataModule;
import com.afterlogic.aurora.drive.data.common.network.p8.P8NetworkDataModule;

import dagger.Module;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module(includes = {
        P7NetworkDataModule.class,
        P8NetworkDataModule.class
})
public class NetworkDataModule {

}
