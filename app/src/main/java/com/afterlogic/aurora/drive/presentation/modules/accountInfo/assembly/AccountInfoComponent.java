package com.afterlogic.aurora.drive.presentation.modules.accountInfo.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.view.AccountInfoActivity;

import dagger.Subcomponent;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = AccountInfoModule.class)
public interface AccountInfoComponent extends MVVMComponent<AccountInfoActivity>{
}
