package com.afterlogic.aurora.drive.presentation.modules.login.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.modules.login.v2.viewModel.LoginViewModel;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class HostFragment extends InjectableMVVMFragment<LoginViewModel> {

    public static HostFragment newInstance() {
        
        Bundle args = new Bundle();
        
        HostFragment fragment = new HostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return DataBindingUtil.inflate(inflater, R.layout.login_host_fragment, container, false);
    }

    @Override
    protected ViewModelProvider createViewModelProvider() {
        return ViewModelProviders.of(getParentFragment());
    }

    @Override
    public LoginViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(LoginViewModel.class);
    }
}
