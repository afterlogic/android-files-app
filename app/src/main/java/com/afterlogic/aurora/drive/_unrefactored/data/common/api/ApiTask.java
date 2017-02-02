package com.afterlogic.aurora.drive._unrefactored.data.common.api;

import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sashka on 31.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class ApiTask<T, R> implements Callback<R>, Task.TaskListener, ApiTaskInterface {

    private T mApiInterface;
    private TaskStateHandler mTaskStateHandler;
    private Callback<R> mCallback;
    private CallCreatorImpl<T, R> mCallCreator;
    private ProgressUpdater mProgressUpdater;
    private boolean mAutoTaskEnd;
    private ApiTaskErrorHandler mErrorHandler;
    private ApiTaskExecuter mApiTaskExecuter;
    private boolean mCallUpdating = false;

    private Task mTask;
    private Call<R> mCall;

    public ApiTask(T apiInterface,
                   ApiTaskExecuter apiTaskExecuter, ProgressUpdater progressUpdater,
                   boolean autoTaskEnd, ApiTaskErrorHandler errorHandler,
                   CallCreatorImpl<T, R> callCreator, Callback<R> callback,
                   TaskStateHandler taskStateHandler) {

        mApiInterface = apiInterface;
        mApiTaskExecuter = apiTaskExecuter;
        mTaskStateHandler = taskStateHandler;
        mProgressUpdater = progressUpdater;
        mAutoTaskEnd = autoTaskEnd;
        mErrorHandler = errorHandler;
        mCallback = callback;
        mCallCreator = callCreator;
    }

    public int execute() {
        int taskId = TaskStateHandler.NO_ID;
        if (mTaskStateHandler != null) {
            mTask = mTaskStateHandler.startNewTask();
            mTask.addTaskListener(this);
            taskId = mTask.getId();
        }

        if (mApiTaskExecuter != null){
            mApiTaskExecuter.onTaskExecute(this);
        }else {
            executeTask();
        }

        return taskId;
    }

    @Override
    public void executeTask() {
        mCallCreator.setTask(this);

        if (mCall == null) {
            mCall = mCallCreator.createCall(mApiInterface);
        }

        if (mCall == null) {
            TaskException ex = new TaskException(TaskException.CALL_NOT_EXIST, "Api call not exist!");
            if (mErrorHandler != null) {
                mErrorHandler.onTaskError(ex);
            } else {
                throw ex;
            }
        }

        if (mProgressUpdater != null && mTask != null) {
            mProgressUpdater.set(mTask, mTaskStateHandler);
        }

        mCall.enqueue(this);
    }

    @Override
    public void cancel() {
        if (mTask != null){
            mTask.cancel();
        }
    }

    @Override
    public void onResponse(Call<R> call, Response<R> response) {
        if (mCallback != null) {
            mCallback.onResponse(call, response);
        }

        if (mCallUpdating) return;

        if (mAutoTaskEnd && mTask != null) {
            mTaskStateHandler.endTask(mTask, true);
        }
    }

    @Override
    public void onFailure(Call<R> call, Throwable t) {
        if (mCallUpdating) return;

        if (mCallback != null) {
            mCallback.onFailure(call, t);
        }

        if (mCallUpdating) return;

        if (mAutoTaskEnd && mTask != null) {
            mTaskStateHandler.endTask(mTask, true);
        }
    }

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
        if (status == Task.EndStatus.CANCELED && mCall != null){
            mCall.cancel();
        }
    }


    public static class Builder<T, R> {

        private Callback<R> mCallback;
        private TaskStateHandler mTaskStateHandler;
        private ProgressUpdater mProgressUpdater;
        private boolean mAutoTaskEnd = true;
        private ApiTaskErrorHandler mApiTaskErrorHandler;
        private ApiTaskExecuter mApiTaskExecuter;
        private T mApi;

        public Builder(T api) {
            mApi = api;
        }

        public Builder<T, R> setCallback(Callback<R> callback) {
            mCallback = callback;
            return this;
        }

        public Builder<T, R> setTaskHandler(TaskStateHandler handler) {
            mTaskStateHandler = handler;
            return this;
        }

        public Builder<T, R> setProgressUpdater(ProgressUpdater listener) {
            mProgressUpdater = listener;
            return this;
        }

        public Builder<T, R> setAutoTaskEnd(boolean autoTaskEnd) {
            mAutoTaskEnd = autoTaskEnd;
            return this;
        }

        public Builder<T, R> setApiTaskErrorHandler(ApiTaskErrorHandler apiTaskErrorHandler) {
            mApiTaskErrorHandler = apiTaskErrorHandler;
            return this;
        }

        public Builder<T, R> setApiTaskExecuter(ApiTaskExecuter apiTaskExecuter) {
            mApiTaskExecuter = apiTaskExecuter;
            return this;
        }

        public ApiTask<T, R> build(CallCreatorImpl<T, R> callCreator) {
            return new ApiTask<>(mApi, mApiTaskExecuter, mProgressUpdater, mAutoTaskEnd,
                    mApiTaskErrorHandler, callCreator, mCallback, mTaskStateHandler
            );
        }
    }

    public static abstract class CallCreatorImpl<T, R> implements CallCreator<T, R>{
        private ApiTask<T, R> mApiTask;

        void setTask(ApiTask<T, R> task){
            mApiTask = task;
        }

        @Nullable
        public CallUpdater<R> startUpdateCall(){
            if (mApiTask == null) return null;

            mApiTask.mCallUpdating = true;
            if (mApiTask.mCall != null){
                mApiTask.mCall.cancel();
            }
            return new CallUpdater<R>() {
                @Override
                public void updateAndReCall(Call<R> call) {
                    mApiTask.mCall = call;
                    mApiTask.mCallUpdating = false;
                    mApiTask.executeTask();
                }
            };
        }
    }

    public interface CallUpdater<R>{
        void updateAndReCall(Call<R> call);
    }

    public static class ProgressUpdater{

        private Task mTask;
        private TaskStateHandler mTaskStateHandler;

        public static ProgressUpdater create(ProgressListener listener){
            return new ProgressUpdater(){
                @Override
                public void notifyChanged(long progress, long max) {
                    super.notifyChanged(progress, max);
                    listener.onProgressChanged(progress, max);
                }
            };
        }

        public void notifyChanged(long progress, long max) {
            if (mTask != null) mTask.notifyChanged(progress, max);
        }

        final public void end(boolean success) {
            if (mTask != null){
                mTaskStateHandler.endTask(mTask, success);
            }
        }

        final void set(Task task, TaskStateHandler taskStateHandler) {
            mTask = task;
            mTaskStateHandler = taskStateHandler;
        }

        public interface ProgressListener{
            void onProgressChanged(long progress, long max);
        }
    }

    public static class TaskException extends RuntimeException{
        public static final int API_NOT_EXIST = 1;
        public static final int CALL_NOT_EXIST = 2;
        int code;

        public TaskException(int code, String detailMessage) {
            super(detailMessage);
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }
}
