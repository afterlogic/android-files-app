package com.afterlogic.aurora.drive.data.common.db;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 15.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class DataBaseModule {

    @Provides @DataScope
    synchronized DataBaseProvider provideDataBase(DataBaseProviderImpl dataBaseProvider){
        return dataBaseProvider;
    }
}
