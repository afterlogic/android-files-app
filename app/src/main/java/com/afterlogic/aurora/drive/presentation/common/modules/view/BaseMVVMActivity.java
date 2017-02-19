package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.presentation.common.modules.viewModel.ViewModel;

import javax.inject.Inject;

/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseMVVMActivity<T extends ViewModel> extends StorableMVVMActivity {

    @Inject
    protected T mViewModel;

    private int mViewModelVariable = BR.viewModel;
    private boolean mAutoBindViewModel = true;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding dataBinding = onCreateBinding(savedInstanceState);

        mViewModel.onViewCreated();
        if (mAutoBindViewModel) {
            dataBinding.setVariable(mViewModelVariable, mViewModel);
        }

        onBindToViewModel(mViewModel);
    }

    protected abstract ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState);

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.onViewStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.onViewStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onViewDestroyed();
        onUnbindViewModel(mViewModel);
    }

    protected void onBindToViewModel(T viewModel){
        //no-op
    }

    private void onUnbindViewModel(T viewModel){
        //no-op
    }

    protected void setViewModelVariable(int viewModelVariable) {
        mViewModelVariable = viewModelVariable;
    }

    protected void setAutoBindViewModel(boolean autoBindViewModel) {
        mAutoBindViewModel = autoBindViewModel;
    }
}
