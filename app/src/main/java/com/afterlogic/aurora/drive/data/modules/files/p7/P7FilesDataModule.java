package com.afterlogic.aurora.drive.data.modules.files.p7;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.file.factory.AuroraFileP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.file.factory.AuroraFileP7MapperFactoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.uploadResult.factory.UploadResultP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.uploadResult.factory.UploadResultP7MapperFactoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.p7.repository.FilesRepositoryP7Impl;
import com.afterlogic.aurora.drive.data.modules.files.p7.service.FilesServiceP7;
import com.afterlogic.aurora.drive.data.modules.files.p7.service.FilesServiceP7Impl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class P7FilesDataModule {

    @Provides
    FilesServiceP7 provideFileService(FilesServiceP7Impl serivce){
        return serivce;
    }

    @Provides @P7
    FilesRepository provideFilesRepository(FilesRepositoryP7Impl repository7){
        return repository7;
    }

    @Provides
    AuroraFileP7MapperFactory provideFileMapper(AuroraFileP7MapperFactoryImpl factory){
        return factory;
    }

    @Provides
    UploadResultP7MapperFactory provideUploadResultMappers(UploadResultP7MapperFactoryImpl factory){
        return factory;
    }
}
