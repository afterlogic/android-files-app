package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

import io.reactivex.Observable;

import static com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncMessenger.PROGRESS;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SyncListener implements Stoppable{

    private static int sListenerId = 0;

    private final Context mContext;
    private final SyncConnection mConnection = new SyncConnection();

    private final BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bindService();
        }
    };

    private final Messenger mIncomingMessenger = new Messenger(new IncomingHandler(this));
    @Nullable
    private Messenger mOutgoingMessenger;

    private boolean mConnected;
    private boolean mBinding;
    private boolean mActive;

    private final SimpleObservableSource<SyncProgress> mProgressSource = new SimpleObservableSource<>();

    private final int mId;

    public SyncListener(Context context) {
        mContext = context;
        mId = ++sListenerId;
    }

    @Override
    public void onStart() {
        bindService();
        mContext.registerReceiver(
                mSyncReceiver,
                new IntentFilter(SyncService.ACTION_SYNC_STARTED)
        );
        mActive = true;
    }

    @Override
    public void onStop() {
        if (!mActive) return;

        if (mConnected){
            mContext.unbindService(mConnection);
        }
        mContext.unregisterReceiver(mSyncReceiver);
        mActive = false;
    }

    public Observable<SyncProgress> getProgressSource() {
        return Observable.defer(() -> mProgressSource);
    }

    private void bindService(){
        if (mBinding) return;
        mBinding = true;

        Intent intent = new Intent(mContext, SyncService.class);
        intent.setAction(SyncService.ACTION_BIND_SYNC_LISTENER);
        boolean binding = mContext.bindService(intent, mConnection, 0);
        mBinding = binding;
    }

    private void onConnected(){
        Message pair = Message.obtain(null, SyncMessenger.PAIR, mId, 0, mIncomingMessenger);
        sendMessage(pair);
    }

    private void sendMessage(Message message){
        if (mOutgoingMessenger == null) return;

        try {
            mOutgoingMessenger.send(message);
        } catch (RemoteException e) {
            MyLog.majorException(e);
        }
    }

    private class SyncConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinding = false;
            mConnected = true;
            mOutgoingMessenger = new Messenger(iBinder);
            onConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinding = false;
            mOutgoingMessenger = null;
            mConnected = false;
        }
    }

    private static class IncomingHandler extends Handler{
        private final SyncListener mSyncListener;

        private IncomingHandler(SyncListener syncListener) {
            mSyncListener = syncListener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    SyncProgress progress = SyncUtil.map(msg.getData());
                    mSyncListener.mProgressSource.onNext(progress);
                    break;
            }
        }
    }
}
