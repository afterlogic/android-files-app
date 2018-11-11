package com.afterlogic.aurora.drive.presentation.modules.start.assembly;

import androidx.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartActivity;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartView;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class StartInjector extends OldBaseInjector<StartActivity, StartView, StartModule> implements Injector<StartActivity> {

    @Inject StartInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected StartModule createModule() {
        return new StartModule();
    }
}
