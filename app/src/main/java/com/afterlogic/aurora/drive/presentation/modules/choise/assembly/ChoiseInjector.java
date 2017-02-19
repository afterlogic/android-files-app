package com.afterlogic.aurora.drive.presentation.modules.choise.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseActivity;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseView;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseInjector extends OldBaseInjector<ChoiseActivity, ChoiseView, ChoiseModule> implements Injector<ChoiseActivity> {

    @Inject
    ChoiseInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected ChoiseModule createModule() {
        return new ChoiseModule();
    }
}
