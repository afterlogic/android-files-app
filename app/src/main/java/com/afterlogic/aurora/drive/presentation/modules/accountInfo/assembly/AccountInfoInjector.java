package com.afterlogic.aurora.drive.presentation.modules.accountInfo.assembly;

import androidx.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.StoredInjector;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.view.AccountInfoActivity;

import javax.inject.Inject;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AccountInfoInjector extends StoredInjector<AccountInfoActivity> {

    @Inject AccountInfoInjector(ModulesComponentCreator componentCreator) {
        super(componentCreator);
    }

    @NonNull
    @Override
    protected MVVMComponent<AccountInfoActivity> createComponent(AccountInfoActivity target, ModulesComponentCreator creator) {
        return creator.accountInfo();
    }
}
