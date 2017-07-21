package com.afterlogic.aurora.drive.presentation.modules.upload.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.UploadActivityBinding;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel.UploadViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 19.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadActivity extends InjectableMVVMActivity<UploadViewModel> implements HasSupportFragmentInjector {

    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentInjector;

    public static Intent intent(Context context) {
        return new Intent(context, UploadActivity.class);
    }

    @Override
    public ViewDataBinding createBinding() {
        UploadActivityBinding binding =  DataBindingUtil.setContentView(this, R.layout.upload_activity);

        setSupportActionBar(binding.toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        return binding;
    }

    @Override
    public UploadViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(UploadViewModel.class);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
