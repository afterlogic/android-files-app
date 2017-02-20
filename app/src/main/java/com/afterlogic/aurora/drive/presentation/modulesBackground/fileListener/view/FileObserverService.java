package com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseService;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.presenter.FileObserverPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 28.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileObserverService extends BaseService implements FileObserverView{

    @Inject @ViewPresenter
    protected FileObserverPresenter mPresenter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void assembly(InjectorsComponent modulesFactory) {
        modulesFactory.fileObserver().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY_COMPATIBILITY;
    }

}