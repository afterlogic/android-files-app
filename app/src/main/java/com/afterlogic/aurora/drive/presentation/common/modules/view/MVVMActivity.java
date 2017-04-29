package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;

import javax.inject.Inject;


/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class MVVMActivity<VM extends ViewModel> extends AppCompatActivity implements MVVMView {

    private boolean mIsActive;

    @Inject
    VM mViewModel;

    private ViewDataBinding mBinding;

    public abstract void assembly(InjectorsComponent injectors);

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) getApplication();
        assembly(app.getInjectors());

        mBinding = onCreateBinding(savedInstanceState);
        mBinding.setVariable(BR.viewModel, mViewModel);
        onBindingCreated(savedInstanceState);

        if (mViewModel != null) {
            mViewModel.onViewCreated();
        }
    }

    @NonNull
    protected abstract ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState);

    protected void onBindingCreated(@Nullable Bundle savedInstanceState) {
        //no-op
    }

    public <T extends ViewDataBinding> T getBinding() {
        return (T) mBinding;
    }

    public VM getViewModel() {
        return mViewModel;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsActive = true;
        if (mViewModel != null) {
            mViewModel.onViewStart();
        }
    }

    @Override
    protected void onStop() {
        if (mViewModel != null) {
            mViewModel.onViewStop();
        }
        mIsActive = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mViewModel != null) {
            mViewModel.onViewStop();
        }
        mBinding.unbind();
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
}
