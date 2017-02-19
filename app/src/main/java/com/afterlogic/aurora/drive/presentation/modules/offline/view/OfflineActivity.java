package com.afterlogic.aurora.drive.presentation.modules.offline.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.OfflineViewModel;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineActivity extends BaseMVVMActivity<OfflineViewModel> {

    @Override
    public void assembly(InjectorsComponent injectors) {
        injectors.offline().inject(this);
    }

    @Override
    protected ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState) {
        return DataBindingUtil.setContentView(this, R.layout.activity_offline);
    }
}
