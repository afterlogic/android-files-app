package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListView;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFileListInjector extends OldBaseInjector<MainFileListFragment, MainFileListView, MainFileListModule> implements Injector<MainFileListFragment> {

    @Inject
    MainFileListInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected MainFileListModule createModule() {
        return new MainFileListModule();
    }
}
