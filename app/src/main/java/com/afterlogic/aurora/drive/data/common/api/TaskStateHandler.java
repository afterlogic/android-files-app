package com.afterlogic.aurora.drive.data.common.api;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sashka on 24.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class TaskStateHandler{
    public static final String ACTION_TASK_ENDED = "TaskStateHandler.ACTION_TASK_ENDED";

    public static final String EXTRA_TASK_ID =
            TaskStateHandler.class.getName() + ".EXTRA_TASK_ID";

    public static final String EXTRA_SUCCES =
            TaskStateHandler.class.getName() + ".SUCCESS";

    public static final int NO_ID = -1;
    public static final String TASK =
            TaskStateHandler.class.getName() + ".TASK";

    private Context mContext;

    private HashMap<Integer, Task> mTasks = new HashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private int mLastId = NO_ID;

    private Task.TaskListener mTaskEndListener = new Task.TaskListener() {
        @Override
        public void onStart(Task task) {
            //Stub
        }

        @Override
        public void onProgressChanged(Task task, long progress, long maxProgress) {
            //Stub
        }

        @Override
        public void onEnd(Task task, Task.EndStatus status) {
            onTaskEnd(task, status);
        }
    };

    public TaskStateHandler(Context context) {
        mContext = context;
    }

    /**
     * Get {@link Task} by id.
     * @param id - task id.
     * @return - requested task or null.
     */
    public Task getTask(int id){
        return mTasks.get(id);
    }

    public List<Task> findTaskWithRoot(int id){
        List<Task> result = new ArrayList<>();
        for (Map.Entry<Integer, Task> entry: mTasks.entrySet()){
            if (entry.getValue().getRootId() == id){
                result.add(entry.getValue());
            }
        }
        return result;
    }

    /**
     * Create new task with next id and add to it default notifyEnd listener.
     * Visible only in package for prevent unnecessary access.
     * @return new {@link Task}
     */
    public Task startNewTask(){
        do {
            mLastId++;
            if (mLastId < 0){
                mLastId = 0;
            }
        }while (mLastId == NO_ID || mTasks.containsKey(mLastId));

        Task task = new Task(mLastId);
        task.addTaskListener(mTaskEndListener);
        task.setNotifyHandler(mHandler);
        mTasks.put(mLastId, task);
        return task;
    }

    /**
     * End task with result.
     */
    void endTask(@NonNull Task task, boolean success){
        task.notifyEnd(success ? Task.EndStatus.SUCCESS : Task.EndStatus.FAILED);
    }

    private void onTaskEnd(Task task, Task.EndStatus status){
        mTasks.remove(task.getId());
        sendEndTaskMessage(task, status);
    }

    private void sendEndTaskMessage(Task task, Task.EndStatus status){
        Intent intent  = new Intent(ACTION_TASK_ENDED);
        intent.putExtra(EXTRA_TASK_ID, task.getId());
        intent.putExtra(EXTRA_SUCCES, status == Task.EndStatus.SUCCESS);
        intent.putExtra(TASK, task);

        LocalBroadcastManager.getInstance(mContext)
                .sendBroadcast(intent);
    }
}
