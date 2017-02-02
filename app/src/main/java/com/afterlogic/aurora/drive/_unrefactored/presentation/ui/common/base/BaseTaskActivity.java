package com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive._unrefactored.core.util.task.TaskProgressNotifier;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Task;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.TaskStateHandler;

import java.text.NumberFormat;

/**
 * Created by sashka on 31.03.16.
 * mail: sunnyday.development@gmail.com
 */
public abstract class BaseTaskActivity extends SingleFragmentActivity  implements
        Task.TaskListener {
    private static final String EXTRA_TASK_ID =
            BaseTaskActivity.class.getName() + ".EXTRA_TASK_ID";

    private static final int TASK_NOT_EXECUTED = -2;

    private static final String EXTRA_MAX_PROGRESS =
            BaseTaskActivity.class.getName() + "EXTRA_MAX_PROGRESS";
    public static final int UNKNOWN_MAX_PROGRESS = -1;

    //UI References
    private ProgressDialog mProgressDialog;

    private int mRootTaskId = TASK_NOT_EXECUTED;
    private int mLastTaskId = TASK_NOT_EXECUTED;
    private long mMaxProgress = -1;
    private boolean mProgressInDialog = false;

    private BroadcastReceiver mTaskUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleTaskEnd(intent);
        }
    };

    private boolean mRestoredWithEmptyPreviousTask = false;

    ////////////////////////////////////////////////
    // [START Override super class] // <editor-fold desc="Override super class">
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Lifecycle] // <editor-fold desc="Lifecycle">
    ////////////////////////////////////////////////

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            onRestoreTask(savedInstanceState);
        }

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        mTaskUpdateReceiver,
                        new IntentFilter(TaskStateHandler.ACTION_TASK_ENDED)
                );
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressInDialog = true;
        if (isTaskProceeding()) {
            Task task = Api.getTaskStateHandler().getTask(mRootTaskId);
            task.addTaskListener(this);
            TaskProgressNotifier.notifyChangeVisibility(false, mRootTaskId, this);
            showProgress(task);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRestoredWithEmptyPreviousTask){
            mRestoredWithEmptyPreviousTask = false;
            onPreviousTaskNotRestored();
        }
    }

    @Override
    protected void onStop() {
        hideProgress();
        Task task = Api.getTaskStateHandler().getTask(mRootTaskId);
        if (task != null) {
            task.removeTaskListener(this);
            TaskProgressNotifier.notifyChangeVisibility(true, mRootTaskId, this);
        }
        mProgressInDialog = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mTaskUpdateReceiver);
        super.onDestroy();
    }

    ////////////////////////////////////////////////
    // [END Lifecycle] // </editor-fold>
    ////////////////////////////////////////////////

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_TASK_ID, mRootTaskId);
        outState.putLong(EXTRA_MAX_PROGRESS, mMaxProgress);
    }

    ////////////////////////////////////////////////
    // [END Override super class] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Implementation] // <editor-fold desc="Implementation">
    ////////////////////////////////////////////////

    @Override
    public void onStart(Task task) {
        onTaskStart(task.getId(), -1);
    }

    /**
     * {@link Task.TaskListener#onProgressChanged(Task, long, long)}  implementation.
     */
    @Override
    public void onProgressChanged(Task task, long progress, long maxProgress) {
        if (task.getId() != mRootTaskId) return;

        if (mProgressInDialog) {
            updateProgressDialog(task, progress, maxProgress);
        }
    }

    @Override
    public void onEnd(Task task, Task.EndStatus status) {
        onTaskEnd(status == Task.EndStatus.SUCCESS, task);
    }

    ////////////////////////////////////////////////
    // [END Implementation] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Own methods] // <editor-fold desc="Own methods">
    ////////////////////////////////////////////////

    protected void onPreviousTaskNotRestored(){
        //stub
    }

    protected boolean isTaskProceeding(){
        return mRootTaskId != TASK_NOT_EXECUTED && Api.getTaskStateHandler().getTask(mRootTaskId) != null;
    }

    /**
     * Restore task values from saved instance state {@link Bundle}
     *
     * @param savedInstanceState - current saved instance state.
     */
    public void onRestoreTask(Bundle savedInstanceState) {
        mRootTaskId = savedInstanceState.getInt(EXTRA_TASK_ID, TASK_NOT_EXECUTED);
        //Cause it may be splitted id try get task
        TaskStateHandler taskStateHandler = Api.getTaskStateHandler();
        Task task = taskStateHandler.getTask(mRootTaskId);
        if (task == null){
            int withRootCount = taskStateHandler.findTaskWithRoot(mRootTaskId).size();
            if (withRootCount == 0) {
                if (mRootTaskId != TASK_NOT_EXECUTED) {
                    mRestoredWithEmptyPreviousTask = true;
                }
                mRootTaskId = TASK_NOT_EXECUTED;
            }
        }
        mMaxProgress = savedInstanceState.getLong(EXTRA_MAX_PROGRESS, -1);
    }

    /**
     * Trigger task start. Show progress, prepare values.
     *
     * @param taskId - started task id.
     * @param maxProgress - task max progress.
     */
    public void onTaskStart(int taskId, long maxProgress) {
        Task task = Api.getTaskStateHandler().getTask(taskId);
        Log.d(getClass().getSimpleName(), "onTaskStart: " + task);
        if (task != null) {

            if (mRootTaskId == TASK_NOT_EXECUTED) {
                onFirstTask(task, maxProgress);
            } else {
                onContinuousTask(task, maxProgress);
            }

            if (mProgressInDialog) {
                task.addTaskListener(this);
                showProgress(task);
            }

            addNotifyProgressUpdaterToTask(task);
            mLastTaskId = taskId;
        } else {
            onTaskEnd(false, null);
        }
    }

    private void onFirstTask(Task task, long maxProgress){
        mLastTaskId = mRootTaskId = task.getId();
        mMaxProgress = maxProgress;
    }

    /**
     * Trigger continuous task (notifyEnd current, start next in one stack).
     *
     * @param task - next task.
     * @param maxProgress - new task max progress.
     */
    private void onContinuousTask(Task task, long maxProgress) {
        task.setRootId(mRootTaskId);

        //Hide previous task notification
        TaskProgressNotifier.notifyChangeVisibility(false, mLastTaskId, this);
        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        nm.cancel(mLastTaskId);

        mMaxProgress = maxProgress;
    }

    /**
     * Add {@link TaskProgressNotifier}'s task listeners to current {@link Task}.
     *
     * @param task - started task.
     */
    protected void addNotifyProgressUpdaterToTask(Task task){
        TaskProgressNotifier taskProgressNotifier = onCreateNotifyProgressUpdater(task);
        if (taskProgressNotifier != null) {
            task.addTaskListener(taskProgressNotifier);
            taskProgressNotifier.startListenProgress(!mProgressInDialog);
        }
    }

    /**
     * Trigger task notifyEnd. Clear values, hide progress.
     *
     * @param success - task notifyEnd result.
     */
    public void onTaskEnd(boolean success, Task task) {
        Log.d(getClass().getSimpleName(), "onTaskEnd: " + task);
        int wihtRootCount = Api.getTaskStateHandler().findTaskWithRoot(mRootTaskId).size();
        boolean end = task == null || wihtRootCount == 0;
        if (end) {
            mRootTaskId = TASK_NOT_EXECUTED;
            mMaxProgress = UNKNOWN_MAX_PROGRESS;
            mLastTaskId = TASK_NOT_EXECUTED;
            hideProgress();
        }
    }

    public void onTaskEnd(boolean success){
        onTaskEnd(success, Api.getTaskStateHandler().getTask(mRootTaskId));
    }

    /**
     * Handle task notifyEnd intent.
     *
     * @param intent - task notifyEnd intent (thrown by {@link TaskStateHandler}).
     */
    private void handleTaskEnd(Intent intent) {
        int taskId = intent.getIntExtra(TaskStateHandler.EXTRA_TASK_ID, TaskStateHandler.NO_ID);

        //Handle notifyEnd only for current task and only if it is not splitted with next task
        if (taskId == mRootTaskId) {
            if (Api.getTaskStateHandler().getTask(taskId) == null) {
                boolean success = intent.getBooleanExtra(TaskStateHandler.EXTRA_SUCCES, false);
                Task task = intent.getParcelableExtra(TaskStateHandler.TASK);
                onTaskEnd(success, task);
            }
        }
    }

    /**
     * Show progress dialog.
     */
    private void showProgress(Task task) {
        if (mProgressDialog == null) {
            mProgressDialog = onCreateProgressDialog();
            mProgressDialog.show();
        }
        updateProgressDialog(task, 0, mMaxProgress);
    }

    /**
     * Update progress indicator by progress values.
     *
     * @param progress - current progress.
     * @param maxProgress - max progress.
     */
    private void updateProgressDialog(Task task, long progress, long maxProgress){
        if (mProgressDialog != null) {
            maxProgress = maxProgress == UNKNOWN_MAX_PROGRESS ? mMaxProgress : maxProgress;

            if (maxProgress != UNKNOWN_MAX_PROGRESS) {
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax((int) maxProgress);
                mProgressDialog.setProgress((int) progress);
                mProgressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
                mProgressDialog.setProgressNumberFormat("%1d/%2d");
            } else {
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressPercentFormat(null);
                mProgressDialog.setProgressNumberFormat(null);
            }
            onProgressDialogUpdate(task, mProgressDialog);
        }
    }

    protected void onProgressDialogUpdate(Task task, ProgressDialog progressDialog){
        //Stub for other activities
    }

    /**
     * Create progress dialog.
     *
     * @return result progress dialog for runned task.
     */
    public ProgressDialog onCreateProgressDialog() {
        ProgressDialog progressDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            progressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        } else {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.dialog_prompt_processing));
        return progressDialog;
    }

    /**
     * Hide progress dialog.
     */
    private void hideProgress() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }

    /**
     * Create notification progress shower. By default it is null (no need show notify).
     * @param task - for that task request notify updater.
     * @return result {@link TaskProgressNotifier} for current task.
     */
    public TaskProgressNotifier onCreateNotifyProgressUpdater(Task task){
        return null;
    }

    /**
     * Get current task id.
     * @return current task id.
     */
    @Override
    public int getTaskId() {
        return mRootTaskId;
    }

    ////////////////////////////////////////////////
    // [END Own methods] // </editor-fold>
    ////////////////////////////////////////////////
}