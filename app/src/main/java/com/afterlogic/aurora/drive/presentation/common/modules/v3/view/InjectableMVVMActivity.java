package com.afterlogic.aurora.drive.presentation.common.modules.v3.view;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.application.assembly.Injectable;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.AppCoreActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public abstract class InjectableMVVMActivity<VM extends LifecycleViewModel>
        extends AppCoreActivity
        implements LifecycleRegistryOwner, Injectable {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private UnbindableObservable.Bag startedBindingsBag = new UnbindableObservable.Bag();
    private UnbindableObservable.Bag createdBindingsBag = new UnbindableObservable.Bag();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private VM viewModel;

    private int viewModelVariable = BR.vm;

    private ViewDataBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onPrepareCreations();

        ViewModelProvider viewModelProvider = createViewModelProvider();
        viewModel = createViewModel(viewModelProvider);

        getLifecycle().addObserver(viewModel);

        binding = createBinding();
        binding.setVariable(viewModelVariable, viewModel);
        binding.executePendingBindings();

        bindCreated(viewModel, createdBindingsBag);
    }

    protected void onPrepareCreations() {
        //no-op
    }

    public abstract ViewDataBinding createBinding();

    @Override
    protected void onStart() {
        super.onStart();
        bindStarted(viewModel, startedBindingsBag);
    }

    @Override
    protected void onStop() {
        startedBindingsBag.unbindAndClear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        createdBindingsBag.unbindAndClear();
        binding.unbind();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    public abstract VM createViewModel(ViewModelProvider provider);

    protected ViewModelProvider createViewModelProvider() {
        return ViewModelProviders.of(this, viewModelFactory);
    }

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
