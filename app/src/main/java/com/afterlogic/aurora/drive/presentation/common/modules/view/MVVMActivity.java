package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;
import com.afterlogic.aurora.drive.presentation.common.util.UnbindableObservable;

import java.util.UUID;

import javax.inject.Inject;


/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class MVVMActivity<VM extends ViewModel> extends AuroraActivity implements StoreableMVVMView {

    private boolean mIsActive;

    private ModuleStoreController mModuleStoreController;

    private final UnbindableObservable.Bag mStartedUnbindable = new UnbindableObservable.Bag();
    private final UnbindableObservable.Bag mCreatedUnbindable = new UnbindableObservable.Bag();

    @Inject
    VM mViewModel;

    private ViewDataBinding mBinding;

    public abstract void assembly(InjectorsComponent injectors);

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) getApplication();
        InjectorsComponent injectors = app.getInjectors();

        mModuleStoreController = injectors.getStoreController();
        mModuleStoreController.onViewCreate(savedInstanceState);

        assembly(app.getInjectors());
        mBinding = onCreateBinding(savedInstanceState);
        onBindingCreated(savedInstanceState);

        if (mViewModel != null) {
            onViewModelCreated(mViewModel, savedInstanceState);
            mViewModel.onViewCreated();
            onBindCreatedBindings(mViewModel, mCreatedUnbindable);
        }

        mBinding.setVariable(BR.viewModel, mViewModel);
    }

    @NonNull
    protected abstract ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState);

    protected void onBindingCreated(@Nullable Bundle savedInstanceState) {
        //no-op
    }

    protected void onViewModelCreated(VM vm, @Nullable Bundle savedInstanceState) {
        //no-op
    }

    protected void onBindCreatedBindings(VM vm, UnbindableObservable.Bag bag) {
        //no-op
    }

    protected void onBindStartedBindings(VM vm, UnbindableObservable.Bag bag) {
        //no-op
    }

    protected void onUnbindStartedBindings(VM vm, UnbindableObservable.Bag bag) {
        bag.unbindAndClear();
    }

    protected void onUnbindCreatedBindings(VM vm, UnbindableObservable.Bag bag) {
        bag.unbindAndClear();
    }

    public <T extends ViewDataBinding> T getBinding() {
        return (T) mBinding;
    }

    public VM getViewModel() {
        return mViewModel;
    }

    @Override
    public UUID getStoreUuid() {
        return mModuleStoreController.getStoreUuid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsActive = true;
        if (mViewModel != null) {
            onBindStartedBindings(mViewModel, mStartedUnbindable);
            mViewModel.onViewStart();
        }
    }

    @Override
    protected void onStop() {
        if (mViewModel != null) {
            mViewModel.onViewStop();
            onUnbindStartedBindings(mViewModel, mStartedUnbindable);
        }
        mIsActive = false;
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mModuleStoreController.onSaveInstance(outState);
    }

    @Override
    protected void onDestroy() {
        if (mViewModel != null) {
            mViewModel.onViewStop();
            onUnbindCreatedBindings(mViewModel, mCreatedUnbindable);
        }
        mBinding.unbind();

        if (isFinishing()) {
            mModuleStoreController.onFinallyDestroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Utils

    protected static  <T extends Observable> void bind(T field,
                                    UnbindableObservable.UnbindableObservableListener<T> listener,
                                    UnbindableObservable.Bag bag) {
        UnbindableObservable.bind(field)
                .addListener(listener)
                .addTo(bag)
                .notifyChanged();
    }
}
