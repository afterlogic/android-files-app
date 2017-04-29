package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding dataBinding = onCreateBinding(savedInstanceState);

        onViewModelCreated(mViewModel, savedInstanceState);
        mViewModel.onViewCreated();
        if (mAutoBindViewModel) {
            dataBinding.setVariable(mViewModelVariable, mViewModel);
        }
    }

    protected abstract ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState);

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.onViewStart();
        onBindToViewModel(mViewModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.onViewStop();
        onUnbindViewModel(mViewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onViewDestroyed();
    }

    protected void onViewModelCreated(T viewModel, Bundle savedInstanceState){
        //no-op
    }

    protected void onBindToViewModel(T viewModel){
        //no-op
    }

    protected void onUnbindViewModel(T viewModel){
        //no-op
    }

    protected void setViewModelVariable(int viewModelVariable) {
        mViewModelVariable = viewModelVariable;
    }

    protected void setAutoBindViewModel(boolean autoBindViewModel) {
        mAutoBindViewModel = autoBindViewModel;
    }

    protected T getViewModel() {
        return mViewModel;
    }
}
