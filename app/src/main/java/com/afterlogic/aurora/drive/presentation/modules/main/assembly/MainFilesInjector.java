package com.afterlogic.aurora.drive.presentation.modules.main.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules.main.view.MainFilesView;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesInjector extends OldBaseInjector<MainFilesActivity, MainFilesView, MainFilesModule> implements Injector<MainFilesActivity> {

    @Inject
    MainFilesInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected MainFilesModule createModule() {
        return new MainFilesModule();
    }
}
