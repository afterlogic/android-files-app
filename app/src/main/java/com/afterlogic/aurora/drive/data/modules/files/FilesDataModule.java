package com.afterlogic.aurora.drive.data.modules.files;

import android.content.Context;
import android.os.Environment;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.ApiConfigurator;
import com.afterlogic.aurora.drive.data.common.util.MultiApiUtil;
import com.afterlogic.aurora.drive.data.modules.files.mapper.general.FileMapperFactoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.mapper.general.FilesMapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.repository.FileRepositoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.repository.FileSubRepository;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesLocalService;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesLocalServiceImpl;

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
    public static final String DOWNLOADS_DIR = "downloads";

    @Provides
    FileSubRepository subRepository(ApiConfigurator configurator, @P7 Provider<FileSubRepository> p7, @P8 Provider<FileSubRepository> p8){
        return MultiApiUtil.chooseByApiVersion(configurator, p7, p8);
    }

    @Provides
    FilesRepository repository(FileRepositoryImpl repository){
        return repository;
    }

    @Provides
    FilesLocalService localService(FilesLocalServiceImpl service){
        return service;
    }

    @Provides
    FilesMapperFactory mapperFactory(FileMapperFactoryImpl mapperFactory){
        return mapperFactory;
    }

    @Provides @Named(CACHE_DIR)
    File cacheDir(Context appContext){
        return new File(appContext.getExternalCacheDir(), "files/");
    }

    @Provides @Named(THUMB_DIR)
    File thumbDir(Context appContext){
        return new File(appContext.getExternalCacheDir(), "thumb/");
    }

    @Provides @Named(OFFLINE_DIR)
    File offlineDir(Context appContext){
        return appContext.getExternalFilesDir("offline");
    }

    @Provides @Named(DOWNLOADS_DIR)
    File dowloadsDir(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }
}
