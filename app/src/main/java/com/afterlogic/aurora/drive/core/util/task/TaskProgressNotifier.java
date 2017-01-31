package com.afterlogic.aurora.drive.core.util.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.data.common.api.Task;
import com.afterlogic.aurora.drive.core.util.NotificationUtil;

import java.util.Locale;

/**
 * Notification task progress shower class.
 */
public class TaskProgressNotifier implements Task.TaskListener{

    private static final int MIN_DELAY = 200;

    public static final String ACTION_PROGRESS_VISIBILITY_CHANGED =
            TaskProgressNotifier.class.getName() + ".ACTION_PROGRESS_VISIBILITY_CHANGED";
    public static final String EXTRA_NOTIFICATION_VISIBILITY =
            TaskProgressNotifier.class.getName() + ".EXTRA_NOTIFICATION_VISIBILITY";
    public static final String EXTRA_TASK_ID =
            TaskProgressNotifier.class.getName() + ".EXTRA_TASK_ID";

    private Context mContext;
    private Title mTitle;
    private Task mTask;

    private boolean mShowNotifications = false;
    private boolean mListenProgress = false;

    private long mLastNotifyTime = 0;

    //Change notification visibility receiver
    private BroadcastReceiver mVisibilityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
            if (mTask.getId() == taskId) {
                boolean notifyVisible = intent.getBooleanExtra(EXTRA_NOTIFICATION_VISIBILITY, false);
                handleVisibilityChanging(notifyVisible);
            }
        }
    };

    public TaskProgressNotifier(Title title, Task task, Context context) {
        mContext = context;
        mTitle = title;
        mTask = task;
    }

    /**
     * Start listen task progress.
     *
     * @param showNotifications - notification visibility.
     */
    public void startListenProgress(boolean showNotifications){
        mShowNotifications = showNotifications;
        mListenProgress = true;
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mVisibilityReceiver,
                        new IntentFilter(ACTION_PROGRESS_VISIBILITY_CHANGED));
        onProgressChanged(mTask, mTask.getProgress(), mTask.getMaxProgress());
    }

    /**
     * Stop listen task progress.
     *
     * @param hideNotifications - if true hide current notification if it visible.
     */
    public void endListenProgress(boolean hideNotifications){
        if (hideNotifications){
            NotificationManagerCompat nm = NotificationManagerCompat.from(mContext);
            nm.cancel(mTask.getId());
        }

        if (mListenProgress) {
            LocalBroadcastManager.getInstance(mContext)
                    .unregisterReceiver(mVisibilityReceiver);
            mListenProgress = false;
        }
    }


    @Override
    public void onStart(Task task) {

    }

    /**
     * {@link Task.TaskListener#onProgressChanged(Task, long, long)}  implementation.
     *
     * Show task progress notification if must show it.
     */
    @Override
    public void onProgressChanged(Task task, long progress, long maxProgress) {
        long currentTime = SystemClock.elapsedRealtime();
        //Notify only if notify allowed and after MIN_DELAY from last notify time
        if (mShowNotifications && mLastNotifyTime + MIN_DELAY < currentTime) {
            mLastNotifyTime = currentTime;
            NotificationUtil.notifyProgress(
                    mTitle.getTitle(), (int) progress, (int) maxProgress, mTask.getId(), mContext);
        }
    }

    /**
     * {@link Task.TaskListener#onEnd(Task, Task.EndStatus)}  implementation.
     *
     * Show success notify if {@link #mShowNotifications} is true.
     */
    @Override
    public void onEnd(Task task, Task.EndStatus status) {
        endListenProgress(false);

        if (!mShowNotifications) return;

        String message;
        switch (status){
            case SUCCESS:
                message = mContext.getString(R.string.prompt_task_result_succes);
                break;
            case CANCELED:
                message = mContext.getString(R.string.prompt_task_result_cancelled);
                break;
            default:
                message = mContext.getString(R.string.prompt_task_result_failed);
        }
        NotificationUtil.notifyMessage(mTitle.getTitle(), message, mTask.getId(), mContext);
    }

    /**
     * Update notification visibility.
     * @param visible notification visibility. if true show notification.
     */
    private void handleVisibilityChanging(boolean visible){
        mShowNotifications = visible;
        if (mShowNotifications){
            onProgressChanged(mTask, mTask.getProgress(), mTask.getMaxProgress());
        }else{
            NotificationManagerCompat nm = NotificationManagerCompat.from(mContext);
            nm.cancel(mTask.getId());
        }
    }

    /**
     * Send change notification visibility broadcast intent
     * with visibility value and target task id.
     *
     * @param visible - notification visibility.
     * @param taskId - target task id.
     * @param ctx - application context.
     */
    public static void notifyChangeVisibility(boolean visible, int taskId, Context ctx){
        Intent intent = new Intent(ACTION_PROGRESS_VISIBILITY_CHANGED);
        intent.putExtra(EXTRA_NOTIFICATION_VISIBILITY, visible);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        LocalBroadcastManager.getInstance(ctx)
                .sendBroadcast(intent);
    }

    public interface Title{
        String getTitle();
    }

    public static class TaskLabelFormatTitle implements Title{
        private String mStringFormat;
        private Task mTask;

        public TaskLabelFormatTitle(String stringFormat, Task task) {
            mStringFormat = stringFormat;
            mTask = task;
        }

        @Override
        public String getTitle() {
            String label = mTask.getLabel() == null ? "" : mTask.getLabel();
            return String.format(Locale.getDefault(), mStringFormat, label);
        }
    }

    public static class StringTitle implements Title{
        private String mTitle;

        public StringTitle(String title) {
            mTitle = title;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }
    }
}
