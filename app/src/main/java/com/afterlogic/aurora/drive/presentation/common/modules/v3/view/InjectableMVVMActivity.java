package com.afterlogic.aurora.drive.presentation.common.modules.v3.view;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.application.assembly.Injectable;
import com.afterlogic.aurora.drive.application.navigation.AppNavigator;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.ActivityResolver;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;

import javax.inject.Inject;

import ru.terrakok.cicerone.NavigatorHolder;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public abstract class InjectableMVVMActivity<VM extends LifecycleViewModel> extends AppCompatActivity implements LifecycleRegistryOwner, Injectable {

    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    private UnbindableObservable.Bag startedBindingsBag = new UnbindableObservable.Bag();
    private UnbindableObservable.Bag createdBindingsBag = new UnbindableObservable.Bag();

    private int fragmentContainerId = View.NO_ID;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    ActivityResolver activityResolver;

    private VM viewModel;

    private int viewModelVariable = BR.vm;

    private ViewDataBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onPrepareCreations();

        ViewModelProvider viewModelProvider = ViewModelProviders.of(this, viewModelFactory);
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
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (navigatorHolder != null) {
            navigatorHolder.setNavigator(new AppNavigator(this, fragmentContainerId));
        }

        if (activityResolver != null) {
            activityResolver.setResolver(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResolver != null) {
            activityResolver.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (activityResolver != null) {
            activityResolver.onPermissionRequestResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onPause() {
        if (navigatorHolder != null) {
            navigatorHolder.removeNavigator();
        }

        if (activityResolver != null) {
            activityResolver.removeResolver();
        }
        super.onPause();
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

    protected void bindCreated(VM vm, UnbindableObservable.Bag bag) {
        // no-op
    }

    protected void bindStarted(VM vm, UnbindableObservable.Bag bag) {
        // no-op
    }

    public void setFragmentContainerId(int fragmentContainerId) {
        this.fragmentContainerId = fragmentContainerId;
    }

    public void setViewModelVariable(int viewModelVariable) {
        this.viewModelVariable = viewModelVariable;
    }

    protected VM getViewModel() {
        return viewModel;
    }
}
