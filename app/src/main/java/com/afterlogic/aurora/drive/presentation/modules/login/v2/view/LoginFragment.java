package com.afterlogic.aurora.drive.presentation.modules.login.v2.view;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.GeneralContentContainerBinding;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.InjectableMVVMFragment;
import com.afterlogic.aurora.drive.presentation.modules.login.v2.viewModel.LoginViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginFragment extends InjectableMVVMFragment<LoginViewModel> implements HasSupportFragmentInjector {

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private GeneralContentContainerBinding binding;

    @Inject
    protected DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Override
    public ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.general_content_container, container, false);
        return binding;
    }

    @Override
    public LoginViewModel createViewModel(ViewModelProvider provider) {
        return provider.get(LoginViewModel.class);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    protected void bindCreated(LoginViewModel vm, UnbindableObservable.Bag bag) {
        super.bindCreated(vm, bag);

        UnbindableObservable.bind(vm.loginState, bag, field -> {

            FragmentManager fm = getChildFragmentManager();
            Fragment currentFragment = fm.findFragmentById(binding.contentContainer.getId());

            switch (field.get()) {
                case HOST:
                    if (currentFragment instanceof HostFragment) return;
                    fm.beginTransaction()
                            .replace(binding.contentContainer.getId(), HostFragment.newInstance())
                            .commitAllowingStateLoss();
                    break;

                case LOGIN:
                    if (currentFragment instanceof AuthFragment) return;
                    fm.beginTransaction()
                            .replace(binding.contentContainer.getId(), AuthFragment.newInstance())
                            .commitAllowingStateLoss();
                    break;
            }
        });
    }
}
