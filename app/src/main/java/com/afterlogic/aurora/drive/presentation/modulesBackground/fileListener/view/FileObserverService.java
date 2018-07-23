package com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVPService;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.presenter.FileObserverPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 28.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileObserverService extends MVPService implements FileObserverView {

    @Inject @ViewPresenter
    protected FileObserverPresenter mPresenter;

    public static Intent intent(Context context){
        return new Intent(context, FileObserverService.class);
    }

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
