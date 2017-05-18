package com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction;

import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;

import javax.inject.Inject;

/**
 * Created by sashka on 21.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AccountActionInjector implements Injector<AccountActionReceiver> {

    private final ModulesComponentCreator mComponentCreator;

    @Inject AccountActionInjector(ModulesComponentCreator componentCreator) {
        mComponentCreator = componentCreator;
    }

    @Override
    public void inject(AccountActionReceiver target) {
        mComponentCreator.accountActionReceiver().inject(target);
    }
}
