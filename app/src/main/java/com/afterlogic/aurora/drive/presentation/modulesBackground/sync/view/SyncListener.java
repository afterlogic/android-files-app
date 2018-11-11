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
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.reactivex.Observable;

import static com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncMessenger.PROGRESS;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SyncListener implements Stoppable {

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

    private AtomicBoolean mConnected = new AtomicBoolean(false);
    private AtomicBoolean mActive = new AtomicBoolean(false);
    private AtomicBoolean mBinding = new AtomicBoolean(false);

    private final SimpleObservableSource<SyncProgress> mProgressSource = new SimpleObservableSource<>();

    private final int mId;

    @Inject
    public SyncListener(Context context) {
        mContext = context;
        mId = ++sListenerId;
    }

    @Override
    public void onStart() {
        if (mActive.getAndSet(true)) return;

        bindService();
        mContext.registerReceiver(
                mSyncReceiver,
                new IntentFilter(SyncService.ACTION_SYNC_STARTED)
        );
    }

    @Override
    public void onStop() {
        if (!mActive.getAndSet(false)) return;

        if (mConnected.get()){
            try {
                mContext.unbindService(mConnection);
            } catch (Exception e) {
                MyLog.majorException(e);
            }
        }
        mContext.unregisterReceiver(mSyncReceiver);
    }

    public Observable<SyncProgress> getProgressSource() {
        return Observable.defer(() -> mProgressSource);
    }

    private void bindService(){
        if (mBinding.getAndSet(true)) return;

        Intent intent = new Intent(mContext, SyncService.class);
        intent.setAction(SyncService.ACTION_BIND_SYNC_LISTENER);
        boolean successBinding = mContext.bindService(intent, mConnection, 0);
        mBinding.set(successBinding);
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
            mBinding.set(false);
            mConnected.set(true);
            mOutgoingMessenger = new Messenger(iBinder);
            onConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinding.set(false);
            mOutgoingMessenger = null;
            mConnected.set(false);
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
