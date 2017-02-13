package com.afterlogic.aurora.drive.presentation.modules.start.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.start.presenter.StartPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 29.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class StartActivity extends BaseActivity implements StartView{

    @Inject @ViewPresenter
    protected StartPresenter mPresenter;

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.start().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
