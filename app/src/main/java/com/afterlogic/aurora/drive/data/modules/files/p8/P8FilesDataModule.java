package com.afterlogic.aurora.drive.data.modules.files.p8;

import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.p8.repository.FilesRepositoryP8Impl;
import com.afterlogic.aurora.drive.data.modules.files.p8.service.FilesServiceP8;
import com.afterlogic.aurora.drive.data.modules.files.p8.service.FilesServiceP8Impl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class P8FilesDataModule {

    @Provides
    FilesServiceP8 provideFilesService(FilesServiceP8Impl serviceP8){
        return serviceP8;
    }

    @Provides @P8
    FilesRepository provideFileRepository(FilesRepositoryP8Impl repositoryP8){
        return repositoryP8;
    }
}
