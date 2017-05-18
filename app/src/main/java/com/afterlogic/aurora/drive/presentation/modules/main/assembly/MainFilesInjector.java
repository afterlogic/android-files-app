package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.StoredInjector;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesActivity;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesInjector extends StoredInjector<MainFilesActivity> implements Injector<MainFilesActivity> {

    @Inject
    MainFilesInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected MVVMComponent<MainFilesActivity> createComponent(MainFilesActivity target, ModulesComponentCreator creator) {
        return creator.mainFiles();
    }
}
