package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.SparseArray;

import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class SyncMessenger{

    static final int PAIR = 1;
    static final int PROGRESS = 2;

    private final SparseArray<Messenger> mMessengers = new SparseArray<>();

    private Messenger mMessenger = new Messenger(new IncomingHandler(this));

    public IBinder getBinder(){
        return mMessenger.getBinder();
    }

    public void notifyProgress(SyncProgress progress){
        Message message = Message.obtain(null, PROGRESS);
        message.setData(SyncUtil.map(progress));
        sendMessageForAll(message);
    }

    private void sendMessageForAll(Message message){
        synchronized (mMessengers) {
            for (int i = 0; i < mMessengers.size(); i++){
                Messenger target = mMessengers.valueAt(i);
                try {
                    target.send(message);
                } catch (RemoteException e) {
                    mMessengers.removeAt(i);
                }
            }
        }
    }

    private static class IncomingHandler extends Handler{

        private final SyncMessenger mMessenger;

        private IncomingHandler(SyncMessenger messenger) {
            mMessenger = messenger;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PAIR:
                    Messenger messenger = (Messenger) msg.obj;
                    mMessenger.mMessengers.put(msg.arg1, messenger);
            }
        }
    }

}
