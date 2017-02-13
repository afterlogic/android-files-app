package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.BaseInjector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFileListView;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFileListInjector extends BaseInjector<MainFileListFragment, MainFileListView, MainFileListModule> implements Injector<MainFileListFragment> {

    @Inject
    MainFileListInjector(AssembliesAssemblyComponent component) {
        super(component);
    }

    @NonNull
    @Override
    protected MainFileListModule createModule() {
        return new MainFileListModule();
    }
}
