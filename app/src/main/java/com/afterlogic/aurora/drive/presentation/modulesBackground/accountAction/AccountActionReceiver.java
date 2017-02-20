package com.afterlogic.aurora.drive.presentation.modulesBackground.accountAction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.AccountUtil;
import com.afterlogic.aurora.drive.core.common.util.AppUtil;
import com.afterlogic.aurora.drive.data.modules.cleaner.DataCleaner;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by sashka on 29.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AccountActionReceiver extends BroadcastReceiver {

    @Inject
    DataCleaner mDataCleaner;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((App) context.getApplicationContext()).getInjectors().accountActionReceiver().inject(this);

        MyLog.d(this, "onReceive: " + intent);
        if (AccountUtil.getCurrentAccount(context) == null){
            mDataCleaner.cleanAllUserData()
                    .andThen(Completable.fromAction(() -> {
                        context.stopService(FileObserverService.intent(context));
                        AppUtil.setComponentEnabled(FileObserverService.class, false, context);
                    }))
                    .subscribe(() -> {}, MyLog::majorException);
        }
    }
}
