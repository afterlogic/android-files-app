package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;

/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseInjector<T> implements Injector<T> {

    private final ModulesComponentCreator mComponentCreator;

    public BaseInjector(ModulesComponentCreator componentCreator) {
        mComponentCreator = componentCreator;
    }

    @Override
    public void inject(T target) {
        inject(target, mComponentCreator);
    }

    protected abstract void inject(T target, ModulesComponentCreator creator);
}
