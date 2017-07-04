package com.afterlogic.aurora.drive.presentation.common.modules.v3.view;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.application.assembly.Injectable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public abstract class InjectableMVVMFragment<VM extends BaseViewModel> extends Fragment implements LifecycleRegistryOwner, Injectable {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private UnbindableObservable.Bag startedBindingsBag = new UnbindableObservable.Bag();
    private UnbindableObservable.Bag createdBindingsBag = new UnbindableObservable.Bag();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private VM viewModel;

    private int viewModelVariable = BR.vm;

    private ViewDataBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = ViewModelProviders.of(this, viewModelFactory);
        viewModel = createViewModel(viewModelProvider);

        getLifecycle().addObserver(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        bindCreated(viewModel, createdBindingsBag);

        binding = createBinding(inflater, container, savedInstanceState);
        binding.setVariable(viewModelVariable, viewModel);
        return binding.getRoot();
    }

    public abstract ViewDataBinding createBinding(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onStart() {
        super.onStart();
        bindStarted(viewModel, createdBindingsBag);
    }

    @Override
    public void onStop() {
        startedBindingsBag.unbindAndClear();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        createdBindingsBag.unbindAndClear();
        binding.unbind();
        super.onDestroyView();
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    public abstract VM createViewModel(ViewModelProvider provider);

    protected void bindCreated(VM vm, UnbindableObservable.Bag bag) {
        // no-op
    }

    protected void bindStarted(VM vm, UnbindableObservable.Bag bag) {
        // no-op
    }

    public void setViewModelVariable(int viewModelVariable) {
        this.viewModelVariable = viewModelVariable;
    }

    protected VM getViewModel() {
        return viewModel;
    }
}
