package com.afterlogic.aurora.drive.presentation.assembly.modules;

import com.afterlogic.aurora.drive._unrefactored.data.common.ApiProvider;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseActivity;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesFragment;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesFragment;
import com.afterlogic.aurora.drive.presentation.modulesBackground.session.SessionChangedReceiver;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * See {@link ModulesFactoryModule}.
 */
@Subcomponent(modules = {ModulesFactoryModule.class})
public interface ModulesFactoryComponent {

    //TODO remove ApiProvider
    void inject(ApiProvider apiProvider);

    void inject(SessionChangedReceiver sessionChangedReceiver);

    PresentationModulesStore modulesStore();

    Injector<LoginActivity> login();

    Injector<StartActivity> start();

    Injector<MainFilesActivity> main();
    Injector<MainFileListFragment> mainFileList();

    Injector<UploadActivity> upload();
    Injector<UploadFilesFragment> uploadFileList();

    Injector<ChoiseActivity> choise();
    Injector<ChoiseFilesFragment> choiseFileList();

    Injector<SyncService> sync();
}
