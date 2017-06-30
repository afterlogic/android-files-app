package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceViewModel;

/**
 * Created by aleksandrcikin on 28.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceActivity extends InjectableMVVMActivity<ReplaceViewModel> {

    public static Intent intent(Context context) {
        return new Intent(context, ReplaceActivity.class);
    }

    @Override
    public ReplaceViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(ReplaceViewModel.class);
    }
}
