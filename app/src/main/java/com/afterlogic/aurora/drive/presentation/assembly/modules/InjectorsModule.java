package com.afterlogic.aurora.drive.presentation.assembly.modules;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.assembly.AccountInfoInjector;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.view.AccountInfoActivity;
import com.afterlogic.aurora.drive.presentation.modules.choise.assembly.ChoiseFilesInjector;
import com.afterlogic.aurora.drive.presentation.modules.choise.assembly.ChoiseInjector;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseActivity;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesFragment;
import com.afterlogic.aurora.drive.presentation.modules.fileView.assembly.FileViewInjector;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.assembly.LoginInjector;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.main.assembly.MainFileListInjector;
import com.afterlogic.aurora.drive.presentation.modules.main.assembly.MainFilesInjector;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.assembly.OfflineInjector;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;
import com.afterlogic.aurora.drive.presentation.modules.start.assembly.StartInjector;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.assembly.UploadFilesInjector;
import com.afterlogic.aurora.drive.presentation.modules.upload.assembly.UploadInjector;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesFragment;
import com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction.AccountActionInjector;
import com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction.AccountActionReceiver;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.assembly.FileObserverInjector;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.assembly.SyncInjector;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Presentation module's wireframes factory module.
 */
@Module(includes = AutoInjectPresentationModule.class)
public class InjectorsModule {

    private ModulesComponentCreator mPresentationAssemblyComponent;

    public InjectorsModule(ModulesComponentCreator presentationAssemblyComponent) {
        mPresentationAssemblyComponent = presentationAssemblyComponent;
    }

    /**
     * Provide {@link ModulesComponentCreator} for creating and accessing to module's assembly
     * component when create wireframe for it.
     * @return - app configured {@link ModulesComponentCreator}.
     */
    @Provides
    ModulesComponentCreator provideAssemblies(){
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
    Injector<MainFileListFragment> fileList(MainFileListInjector injector){
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

    @Provides
    Injector<SyncService> sync(SyncInjector injector){
        return injector;
    }

    @Provides
    Injector<FileObserverService> fileObserver(FileObserverInjector injector){
        return injector;
    }

    @Provides
    Injector<FileViewActivity> fileView(FileViewInjector injector){
        return injector;
    }

    @Provides
    Injector<OfflineActivity> offline(OfflineInjector injector){
        return injector;
    }

    @Provides
    Injector<AccountActionReceiver> accountActionReceiver(AccountActionInjector injector){
        return injector;
    }

    @Provides
    Injector<AccountInfoActivity> accountInfo(AccountInfoInjector infoInjector){
        return infoInjector;
    }
}
