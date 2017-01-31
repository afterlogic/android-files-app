package com.afterlogic.aurora.drive.data.modules.project7.assembly;

import com.afterlogic.aurora.drive.data.modules.project7.modules.files.mapper.file.factory.AuroraFileP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.project7.modules.files.mapper.file.factory.AuroraFileP7MapperFactoryImpl;
import com.afterlogic.aurora.drive.data.modules.project7.modules.files.mapper.uploadResult.factory.UploadResultP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.project7.modules.files.mapper.uploadResult.factory.UploadResultP7MapperFactoryImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 10.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class P7DataMappersModule {

    @Provides
    AuroraFileP7MapperFactory provideFileMapper(AuroraFileP7MapperFactoryImpl factory){
        return factory;
    }

    @Provides
    UploadResultP7MapperFactory provideUploadResultMappers(UploadResultP7MapperFactoryImpl factory){
        return factory;
    }
}
