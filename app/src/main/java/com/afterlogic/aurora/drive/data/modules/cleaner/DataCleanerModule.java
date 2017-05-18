package com.afterlogic.aurora.drive.data.modules.cleaner;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 21.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module()
public class DataCleanerModule {

    @Provides
    DataCleaner dataCleaner(DataCleanerImpl cleaner){
        return cleaner;
    }
}
