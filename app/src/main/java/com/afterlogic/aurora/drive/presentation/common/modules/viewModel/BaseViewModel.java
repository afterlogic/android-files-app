package com.afterlogic.aurora.drive.presentation.common.modules.viewModel;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;

/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseViewModel implements ViewModel {

    @Override
    public void onViewStart() {
        //no-op
    }

    @Override
    public void onViewStop() {
        //no-op
    }

    @Override
    public void onViewCreated() {
        //no-op
    }

    @Override
    public void onViewDestroyed() {
        //no-op
    }

    public void onErrorObtained(Throwable error) {
        MyLog.majorException(error);
    }
}
