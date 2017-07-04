package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.BaseViewModel;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceViewModel extends BaseViewModel {

    @Inject
    public ReplaceViewModel() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        MyLog.d("On start: " + this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
