package com.afterlogic.aurora.drive._unrefactored.core.util.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.afterlogic.aurora.drive._unrefactored.data.common.api.Task;
import com.afterlogic.aurora.drive._unrefactored.presentation.services.FileLoadService;

/**
 * Created by sashka on 31.05.16.
 * mail: sunnyday.development@gmail.com
 */
public class TaskMessageHandler extends Handler {
    private Task mTask;
    private Messenger mService;

    public TaskMessageHandler(Task task, Messenger service) {
        mTask = task;
        mService = service;
    }

    @Override
    public void handleMessage(final Message msg) {
        switch (msg.what) {
            case FileLoadService.REGISTER_RESULT:
                if (msg.arg1 == FileLoadService.SUCCESS) {
                    mTask.addTaskListener(new Task.TaskListener() {
                        @Override
                        public void onStart(Task task) {

                        }

                        @Override
                        public void onProgressChanged(Task task, long progress, long maxProgress) {

                        }

                        @Override
                        public void onEnd(Task task, Task.EndStatus status) {
                            if (status == Task.EndStatus.CANCELED) {
                                try {
                                    mService.send(Message.obtain(null,
                                            FileLoadService.CANCEL_TASK, task.getId(), 0));
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    mTask.notifyStarted();
                } else {
                    mTask.notifyEnd(Task.EndStatus.CANCELED);
                }
                break;
            case FileLoadService.TASK_PROGRESS:
                Bundle data = msg.getData();
                long progress = data.getLong(FileLoadService.PROGRESS);
                long max = data.getLong(FileLoadService.MAX_PROGRESS);
                String fileName = data.getString(FileLoadService.FILE_NAME);
                mTask.setLabel(fileName);
                mTask.notifyChanged(progress, max);
                break;
            case FileLoadService.TASK_RESULT:
                data = msg.getData();
                mTask.getArgs().putSerializable(
                        Task.TASK_RESULT, data.getSerializable(FileLoadService.FILE)
                );
                break;
            case FileLoadService.TASK_ENDED:
                Task.EndStatus status = msg.arg1 == FileLoadService.SUCCESS ?
                        Task.EndStatus.SUCCESS : Task.EndStatus.FAILED;
                mTask.notifyEnd(status);
                break;
            default:
                super.handleMessage(msg);
        }
    }
}
