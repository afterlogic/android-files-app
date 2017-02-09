package com.afterlogic.aurora.drive.presentation.modulesBackground.fileLoad.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileLoadServiceConnecter implements Stoppable{

    private final Context mContext;
    private final FileLoadServiceReceiver mServiceReceiver = new FileLoadServiceReceiver();

    public FileLoadServiceConnecter(Context context) {
        mContext = context;
    }

    @Override
    public void onStart() {
        mContext.registerReceiver(mServiceReceiver, new IntentFilter(""));
    }

    @Override
    public void onStop() {
        mContext.unregisterReceiver(mServiceReceiver);
    }


    static class FileLoadServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
