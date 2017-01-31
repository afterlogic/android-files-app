package com.afterlogic.aurora.drive.core.util.task;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.afterlogic.aurora.drive.data.common.api.Task;
import com.afterlogic.aurora.drive.presentation.services.FileLoadService;

/**
 * Created by sashka on 31.05.16.
 * mail: sunnyday.development@gmail.com
 */
public class TaskRegisterConnection implements ServiceConnection {

    private Context mContext;
    private Task mTask;

    public TaskRegisterConnection(Task task, Context ctx) {
        mContext = ctx;
        mTask = task;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        registerTaskMessenger(new Messenger(service));
        mContext.unbindService(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    /**
     * Register {@link Messenger} for control task state.
     * @param service - {@link FileLoadService#mMessenger} file service messenger binder.
     */
    private void registerTaskMessenger(Messenger service){
        Message msg = Message.obtain(null, FileLoadService.REGISTER_TASK_HANDLER, mTask.getId(), 0);
        msg.replyTo = new Messenger(new TaskMessageHandler(mTask, service));
        try {
            service.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
