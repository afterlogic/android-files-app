package com.afterlogic.aurora.drive.presentation.modules.start.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.BaseInjector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartActivity;
import com.afterlogic.aurora.drive.presentation.modules.start.view.StartView;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class StartInjector extends BaseInjector<StartActivity, StartView, StartModule> implements Injector<StartActivity> {

    @Inject StartInjector(AssembliesAssemblyComponent component) {
        super(component);
    }

    @NonNull
    @Override
    protected StartModule createModule() {
        return new StartModule();
    }
}
