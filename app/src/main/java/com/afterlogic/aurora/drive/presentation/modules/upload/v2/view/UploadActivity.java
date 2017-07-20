package com.afterlogic.aurora.drive.presentation.modules.upload.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel.UploadViewModel;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadActivity extends InjectableMVVMActivity<UploadViewModel> {

    @Override
    public ViewDataBinding createBinding() {
        return DataBindingUtil.setContentView(this, R.layout.upload_activity);
    }

    @Override
    public UploadViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(UploadViewModel.class);
    }
}
