package com.afterlogic.aurora.drive.presentation.modules.start.view;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVPActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.start.presenter.StartPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 29.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class StartActivity extends MVPActivity implements StartView{

    @Inject @ViewPresenter
    protected StartPresenter mPresenter;

    @Override
    protected void assembly(InjectorsComponent modulesFactory) {
        modulesFactory.start().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
