package com.afterlogic.aurora.drive.presentation.assembly.modules;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ModuleStoreController;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.view.AccountInfoActivity;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseActivity;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesFragment;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewActivity;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartActivity;
import com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction.AccountActionReceiver;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * See {@link InjectorsModule}.
 */
@Subcomponent(modules = {InjectorsModule.class})
public interface InjectorsComponent {

    void inject(App app);

    PresentationModulesStore modulesStore();

    ModuleStoreController getStoreController();

    Injector<StartActivity> start();

    Injector<ChoiseActivity> choise();
    Injector<ChoiseFilesFragment> choiseFileList();

    Injector<SyncService> sync();
    Injector<FileObserverService> fileObserver();

    Injector<FileViewActivity> fileView();

    Injector<AccountActionReceiver> accountActionReceiver();

    Injector<AccountInfoActivity> accountInfo();
}
