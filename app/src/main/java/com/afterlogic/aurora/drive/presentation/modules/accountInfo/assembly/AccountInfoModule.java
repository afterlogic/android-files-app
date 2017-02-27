package com.afterlogic.aurora.drive.presentation.modules.accountInfo.assembly;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMModule;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.model.AccountInfoInteractor;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.model.AccountInfoInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.view.AccountInfoActivity;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.viewModel.AccountInfoVM;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.viewModel.AccountInfoVMImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class AccountInfoModule extends MVVMModule<AccountInfoActivity>{

    @Provides
    AccountInfoVM viewModel(AccountInfoVMImpl viewModel){
        return viewModel;
    }

    @Provides
    AccountInfoInteractor interactor(AccountInfoInteractorImpl infoInteractor){
        return infoInteractor;
    }
}
