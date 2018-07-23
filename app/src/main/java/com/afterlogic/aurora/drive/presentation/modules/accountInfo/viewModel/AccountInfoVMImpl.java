package com.afterlogic.aurora.drive.presentation.modules.accountInfo.viewModel;

import androidx.databinding.ObservableField;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.model.AccountInfoInteractor;

import javax.inject.Inject;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AccountInfoVMImpl extends BaseViewModel implements AccountInfoVM {

    private final AccountInfoInteractor mInteractor;

    private final ObservableField<String> mLogin = new ObservableField<>();
    private final ObservableField<String> mHost = new ObservableField<>();

    @Inject AccountInfoVMImpl(AccountInfoInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onViewStart() {
        super.onViewStart();
        mInteractor.getLogin()
                .subscribe(mLogin::set, MyLog::majorException);
        mInteractor.getHost()
                .subscribe(mHost::set, MyLog::majorException);
    }

    @Override
    public ObservableField<String> getLogin() {
        return mLogin;
    }

    @Override
    public ObservableField<String> getHost() {
        return mHost;
    }
}
