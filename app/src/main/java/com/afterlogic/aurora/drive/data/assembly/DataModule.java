package com.afterlogic.aurora.drive.data.assembly;


import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStoreImpl;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefs;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefsImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Will provideFor repositories, services and etc.
 */
@Module(includes = {
        FilesDataModule.class
})
public class DataModule {

    @Provides @DataScope
    AppPrefs provideAppPrefs(AppPrefsImpl prefsData){
        return prefsData;
    }


    @Provides @DataScope
    SharedObservableStore provideRepositoryCache(SharedObservableStoreImpl sharedObservableStore){
        return sharedObservableStore;
    }
}
