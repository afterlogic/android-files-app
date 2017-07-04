package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.ActivityReplaceBinding;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.ReplaceViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 28.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceActivity extends InjectableMVVMActivity<ReplaceViewModel> implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    public static Intent intent(Context context) {
        return new Intent(context, ReplaceActivity.class);
    }

    @Override
    public ReplaceViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(ReplaceViewModel.class);
    }

    @Override
    public ViewDataBinding createBinding() {
        ActivityReplaceBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_replace);
        binding.setAdapter(new FileTypesPagerAdapter(getSupportFragmentManager()));

        setSupportActionBar(binding.toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        return binding;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    public void onBackPressed() {
        getViewModel().onBackPressed();
    }
}
