package com.afterlogic.aurora.drive.presentation.modules.choise.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesFragment;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesView;

import javax.inject.Inject;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseFilesInjector extends OldBaseInjector<ChoiseFilesFragment, ChoiseFilesView, ChoiseFilesModule> implements Injector<ChoiseFilesFragment> {

    @Inject
    ChoiseFilesInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected ChoiseFilesModule createModule() {
        return new ChoiseFilesModule();
    }
}
