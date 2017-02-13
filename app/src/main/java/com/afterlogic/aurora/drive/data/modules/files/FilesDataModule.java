package com.afterlogic.aurora.drive.data.modules.files;

import android.content.Context;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.ApiConfigurator;
import com.afterlogic.aurora.drive.data.common.util.MultiApiUtil;
import com.afterlogic.aurora.drive.data.modules.files.repository.FileRepositoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.repository.FileSubRepository;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;

import java.io.File;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module(includes = {
        P7FilesDataModule.class,
        P8FilesDataModule.class
})
public class FilesDataModule{

    public static final String CACHE_DIR = "cache";
    public static final String THUMB_DIR = "thumb";
    public static final String OFFLINE_DIR = "offline";

    @Provides
    FileSubRepository subRepository(ApiConfigurator configurator, @P7 Provider<FileSubRepository> p7, @P8 Provider<FileSubRepository> p8){
        return MultiApiUtil.chooseByApiVersion(configurator, p7, p8);
    }

    @Provides
    FilesRepository repository(FileRepositoryImpl repository){
        return repository;
    }

    @Provides @Named(CACHE_DIR)
    File cacheDir(Context appContext){
        return new File(appContext.getExternalCacheDir(), "files/");
    }

    @Provides @Named(THUMB_DIR)
    File thumbDir(Context appContext){
        return new File(appContext.getExternalCacheDir(), "thumb");
    }

    @Provides @Named(OFFLINE_DIR)
    File offlineDir(Context appContext){
        return appContext.getExternalFilesDir("offline");
    }
}
