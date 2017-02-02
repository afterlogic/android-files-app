package com.afterlogic.aurora.drive._unrefactored.data.common.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sashka on 01.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class Task implements Parcelable{
    public static final String TASK_RESULT = Task.class.getName() + ".TASK_RESULT";

    private int mId;
    private long mProgress = -1;
    private long mMaxProgress = -1;
    private int mRootId;

    private Bundle mArgs = new Bundle();

    private String mLabel;

    Task(int id) {
        mId = id;
        mRootId = id;
    }

    private Handler mNotifyHandler;

    protected Task(Parcel in) {
        mId = in.readInt();
        mProgress = in.readLong();
        mMaxProgress = in.readLong();
        mArgs = in.readBundle();
        mLabel = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    private final ArrayList<TaskListener> mProgressListeners = new ArrayList<>();

    public void setNotifyHandler(Handler notifyHandler) {
        mNotifyHandler = notifyHandler;
    }

    public void addTaskListener(TaskListener listener) {
        mProgressListeners.add(listener);
    }

    public void removeTaskListener(TaskListener listener) {
        mProgressListeners.remove(listener);
    }

    public long getMaxProgress() {
        return mMaxProgress;
    }

    public long getProgress() {
        return mProgress;
    }

    public void notifyStarted(){
        Runnable notify = new Runnable() {
            @Override
            public void run() {
                synchronized (mProgressListeners) {
                    List<TaskListener> listeners = new ArrayList<>();
                    listeners.addAll(mProgressListeners);
                    for (TaskListener listener : listeners) {
                        listener.onStart(Task.this);
                    }
                }
            }
        };
        if (mNotifyHandler != null){
            mNotifyHandler.post(notify);
        }else{
            notify.run();
        }
    }

    /**
     * Notify task progress changed in {@link #mNotifyHandler} thread if it exist.
     */
    public void notifyChanged(final long progress, final long maxProgress) {
        mMaxProgress = maxProgress;
        mProgress = progress;
        Runnable notify = new Runnable() {
            @Override
            public void run() {
                synchronized (mProgressListeners) {
                    for (TaskListener listener : mProgressListeners) {
                        listener.onProgressChanged(Task.this, progress, maxProgress);
                    }
                }
            }
        };
        if (mNotifyHandler != null){
            mNotifyHandler.post(notify);
        }else{
            notify.run();
        }
    }

    /**
     * Notify task ended in {@link #mNotifyHandler} thread if it exist.
     */
    public void notifyEnd(final EndStatus status) {
        Runnable notify = () -> {
            synchronized (mProgressListeners) {
                for (TaskListener endListener : mProgressListeners) {
                    endListener.onEnd(Task.this, status);
                }
            }
        };
        if (mNotifyHandler != null){
            mNotifyHandler.post(notify);
        }else{
            notify.run();
        }
    }

    public int getId() {
        return mId;
    }

    /**
     * Cancel task and notify notifyEnd.
     */
    public void cancel(){
        notifyEnd(EndStatus.CANCELED);
    }

    public Bundle getArgs() {
        return mArgs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeLong(mProgress);
        dest.writeLong(mMaxProgress);
        dest.writeBundle(mArgs);
        dest.writeString(mLabel);
    }

    public int getRootId() {
        return mRootId;
    }

    public void setRootId(int rootId) {
        mRootId = rootId;
    }

    public enum EndStatus{
        SUCCESS, FAILED, CANCELED
    }

    public interface TaskListener {
        void onStart(Task task);
        void onProgressChanged(Task task, long progress, long maxProgress);
        void onEnd(Task task, Task.EndStatus status);
    }
}
