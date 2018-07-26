package com.afterlogic.aurora.drive.data.modules.files;

import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.modules.files.repository.FileRepositoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.repository.FileSubRepository;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.repository.P8FilesSubRepositoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesServiceP8;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesServiceP8Impl;

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
    FileSubRepository provideFileRepository(P8FilesSubRepositoryImpl repositoryP8){
        return repositoryP8;
    }

    @P8
    @Provides
    FilesRepository provideFilesRepository(@P8 FileSubRepository subRepository,
                                           FileRepositoryImpl.Factory factory) {
        return factory.create(subRepository);
    }

}
