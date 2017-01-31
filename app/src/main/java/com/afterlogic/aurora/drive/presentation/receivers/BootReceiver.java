package com.afterlogic.aurora.drive.presentation.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.presentation.services.FileObserverService;

/**
 * Created by sashka on 12.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, FileObserverService.class));
    }
}
