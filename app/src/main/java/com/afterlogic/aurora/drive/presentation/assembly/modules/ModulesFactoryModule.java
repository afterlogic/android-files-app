package com.afterlogic.aurora.drive.presentation.assembly.modules;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.choise.assembly.ChoiseFilesInjector;
import com.afterlogic.aurora.drive.presentation.modules.choise.assembly.ChoiseInjector;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseActivity;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesFragment;
import com.afterlogic.aurora.drive.presentation.modules.filelist.assembly.FileListInjector;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.assembly.MainFilesInjector;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.assembly.LoginInjector;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.start.assembly.StartInjector;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.assembly.UploadFilesInjector;
import com.afterlogic.aurora.drive.presentation.modules.upload.assembly.UploadInjector;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Presentation module's wireframes factory module.
 */
@Module
public class ModulesFactoryModule {

    private AssembliesAssemblyComponent mPresentationAssemblyComponent;

    public ModulesFactoryModule(AssembliesAssemblyComponent presentationAssemblyComponent) {
        mPresentationAssemblyComponent = presentationAssemblyComponent;
    }

    /**
     * Provide {@link AssembliesAssemblyComponent} for creating and accessing to module's assembly
     * component when create wireframe for it.
     * @return - app configured {@link AssembliesAssemblyComponent}.
     */
    @Provides
    AssembliesAssemblyComponent provideAssemblies(){
        return mPresentationAssemblyComponent;
    }

    @Provides
    Injector<LoginActivity> login(LoginInjector injector){
        return injector;
    }

    @Provides
    Injector<StartActivity> start(StartInjector injector){
        return injector;
    }

    @Provides
    Injector<MainFilesActivity> filesMain(MainFilesInjector injector){
        return injector;
    }

    @Provides
    Injector<FileListFragment> fileList(FileListInjector injector){
        return injector;
    }

    @Provides
    Injector<UploadActivity> upload(UploadInjector injector){
        return injector;
    }

    @Provides
    Injector<UploadFilesFragment> uploadFilesList(UploadFilesInjector injector){
        return injector;
    }

    @Provides
    Injector<ChoiseActivity> choise(ChoiseInjector injector){
        return injector;
    }

    @Provides
    Injector<ChoiseFilesFragment> choiseFileList(ChoiseFilesInjector injector){
        return injector;
    }
}
