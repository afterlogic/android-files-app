package com.afterlogic.aurora.drive.presentation.modules.choise.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules.choise.presenter.ChoisePresenter;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseViewModel;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseActivity extends BaseFilesActivity<ChoiseViewModel, ChoisePresenter> implements ChoiseView {

    private SimpleListener mLockedListener = new SimpleListener(this::updateActionBar);

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.choise().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateActionBar();

        mViewModel.getLocked().addOnPropertyChangedCallback(mLockedListener);
    }

    @Override
    public BaseFilesListFragment getFilesContent(String type) {
        return new ChoiseFilesFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getLocked().removeOnPropertyChangedCallback(mLockedListener);
    }

    @Override
    public ViewDataBinding onCreateBind() {
        return DataBindingUtil.setContentView(this, R.layout.activity_choise);
    }

    private void updateActionBar(){
        ActionBar ab = getSupportActionBar();
        if (ab == null) return;

        if (!mViewModel.getLocked().get()){
            ab.setHomeAsUpIndicator(R.drawable.ic_close);
        }else{
            ab.setHomeAsUpIndicator(null);
        }
    }

    @Override
    protected void updateHomeButtonByViewModel() {
        //no-op
    }
}
