package com.afterlogic.aurora.drive.data.modules.files;

import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.ApiConfigurator;
import com.afterlogic.aurora.drive.data.common.assembly.MultiApiModule;
import com.afterlogic.aurora.drive.data.modules.files.p7.P7FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.p8.P8FilesDataModule;

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
public class FilesDataModule extends MultiApiModule{

    public FilesDataModule(ApiConfigurator apiConfigurator) {
        super(apiConfigurator);
    }

    @Provides
    FilesRepository repository(@P7 Provider<FilesRepository> p7, @P8 Provider<FilesRepository> p8){
        return chooseByApiVersion(p7, p8);
    }
}
