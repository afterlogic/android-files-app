package com.afterlogic.aurora.drive.presentation.modules.offline.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.StoredInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.offline.view.OfflineActivity;

import javax.inject.Inject;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineInjector extends StoredInjector<OfflineActivity> implements Injector<OfflineActivity> {

    @Inject
    public OfflineInjector(ModulesComponentCreator componentCreator) {
        super(componentCreator);
    }

    @NonNull
    @Override
    protected MVVMComponent<OfflineActivity> createComponent(OfflineActivity target, ModulesComponentCreator creator) {
        return creator.offline();
    }
}
