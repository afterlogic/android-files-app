package com.afterlogic.aurora.drive.presentation.assembly.wireframes;

import com.afterlogic.aurora.drive._unrefactored.data.common.ApiProvider;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartActivity;
import com.afterlogic.aurora.drive.presentation.modulesBackground.session.SessionChangedReceiver;

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

    Injector<MainFilesActivity> filesMain();

    Injector<FileListFragment> fileList();
}
