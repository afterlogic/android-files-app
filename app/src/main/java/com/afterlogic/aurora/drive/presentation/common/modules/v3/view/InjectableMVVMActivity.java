package com.afterlogic.aurora.drive.presentation.common.modules.v3.view;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afterlogic.aurora.drive.application.assembly.Injectable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.common.util.UnbindableObservable;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public abstract class InjectableMVVMActivity<VM extends BaseViewModel> extends AppCompatActivity implements LifecycleRegistryOwner, Injectable {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private UnbindableObservable.Bag startedBindingsBag = new UnbindableObservable.Bag();
    private UnbindableObservable.Bag createdBindingsBag = new UnbindableObservable.Bag();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private VM viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider viewModelProvider = ViewModelProviders.of(this, viewModelFactory);

        viewModel = createViewModel(viewModelProvider);

        getLifecycle().addObserver(viewModel);
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

}
