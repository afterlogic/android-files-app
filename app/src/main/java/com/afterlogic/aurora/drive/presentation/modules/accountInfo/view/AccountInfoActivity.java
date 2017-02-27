package com.afterlogic.aurora.drive.presentation.modules.accountInfo.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.ActivityAccountInfoBinding;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.viewModel.AccountInfoVM;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AccountInfoActivity extends BaseMVVMActivity<AccountInfoVM> {

    @Override
    public void assembly(InjectorsComponent injectors) {
        injectors.accountInfo().inject(this);
    }

    @Override
    protected ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState) {
        ActivityAccountInfoBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_account_info);
        setSupportActionBar(binding.toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }
        return binding;
    }
}
