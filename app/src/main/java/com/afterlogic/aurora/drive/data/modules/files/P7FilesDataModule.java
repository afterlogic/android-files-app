package com.afterlogic.aurora.drive.data.modules.files;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.modules.files.mapper.p7.file.factory.AuroraFileP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.mapper.p7.file.factory.AuroraFileP7MapperFactoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.mapper.p7.uploadResult.factory.UploadResultP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.mapper.p7.uploadResult.factory.UploadResultP7MapperFactoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.repository.FileSubRepository;
import com.afterlogic.aurora.drive.data.modules.files.repository.P7FileSubRepositoryImpl;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesServiceP7;
import com.afterlogic.aurora.drive.data.modules.files.service.FilesServiceP7Impl;

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
    FileSubRepository provideFilesRepository(P7FileSubRepositoryImpl repository7){
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
