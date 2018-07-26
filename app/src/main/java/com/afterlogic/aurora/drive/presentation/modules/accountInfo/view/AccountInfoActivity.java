package com.afterlogic.aurora.drive.presentation.modules.accountInfo.view;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.ActivityAccountInfoBinding;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.accountInfo.viewModel.AccountInfoVM;

/**
 * Created by sashka on 27.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AccountInfoActivity extends MVVMActivity<AccountInfoVM> {

    public static Intent intent(Context context) {
        return new Intent(context, AccountInfoActivity.class);
    }

    @Override
    public void assembly(InjectorsComponent injectors) {
        injectors.accountInfo().inject(this);
    }

    @Override
    @NonNull
    protected ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState) {
        return DataBindingUtil.setContentView(this, R.layout.activity_account_info);
    }

    @Override
    protected void onBindingCreated(@Nullable Bundle savedInstanceState) {
        super.onBindingCreated(savedInstanceState);
        ActivityAccountInfoBinding binding = getBinding();
        setSupportActionBar(binding.toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
}
