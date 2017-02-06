package com.afterlogic.aurora.drive._unrefactored.presentation.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.DeepFolderObserver;
import com.afterlogic.aurora.drive._unrefactored.core.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.interfaces.FileEventListener;
import com.afterlogic.aurora.drive._unrefactored.core.util.task.TaskProgressNotifier;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Task;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by sashka on 28.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileObserverService extends Service implements FileEventListener {
    private static final String TAG = FileObserverService.class.getSimpleName();

    private static final int EVENTS = FileObserver.CLOSE_WRITE | FileObserver.ATTRIB |
            FileObserver.CREATE;

    private DeepFolderObserver mOfflineObserver;
    private DeepFolderObserver mCacheObserver;
    private int mLogPathOffset;

    private LocalBinder mBinder = new LocalBinder();

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mRequstId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        File ext = getExternalFilesDir(null);
        if (ext != null) {
            mLogPathOffset = ext.getAbsolutePath().length();
        }

        Log.d(TAG, "Service started.");
        File offline = FileUtil.getOfflineFileDir(this);
        if (offline.exists() || offline.mkdirs()) {
            mOfflineObserver = new DeepFolderObserver(offline, EVENTS, this);
            mOfflineObserver.startWatching();
        }

        File cacheFile = FileUtil.getCacheFileDir(this);
        if (cacheFile.exists() || cacheFile.mkdirs()) {
            mCacheObserver = new DeepFolderObserver(cacheFile, EVENTS, this);
            mCacheObserver.startWatching();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        if (mOfflineObserver != null) {
            mOfflineObserver.stopWatching();
        }
        if (mCacheObserver != null) {
            mCacheObserver.stopWatching();
        }
        Log.d(TAG, "Service stopped.");
        super.onDestroy();
    }

    @Override
    public void onEvent(int event, final String path) {
        Log.d(TAG, "Event " +
                getEventDescription(event) + " for: " + path.substring(mLogPathOffset));

        if (event == FileObserver.CLOSE_WRITE || event == FileObserver.ATTRIB){
            DBHelper db = new DBHelper(this);
            WatchingFileDAO dao = db.getWatchingFileDAO();

            File local = new File(path);
            WatchingFile watching = null;
            try {
                 watching = dao.queryBuilder()
                        .where()
                        .eq(WatchingFile.COLUMN_LOCAL_FILE, path)
                        .queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (watching != null) {

                long localLastModified = local.lastModified();
                long watchingLastModified = watching.getLastModified();

                if (localLastModified != watchingLastModified){
                    //Sync after delay only actual request
                    final String spec = watching.getRemoteUniqueSpec();

                    mRequstId++;
                    final int currentRequest = mRequstId;
                    mHandler.postDelayed(() -> {
                        Log.d(TAG, "Delayed sync for: " + spec);
                        if (currentRequest == mRequstId) {
                            sync(spec);
                        }
                    }, 1000);
                }
            }
        }
    }

    /**
     * Upload changes to server.
     */
    private void sync(String path){
        AccountManager ac = AccountManager.get(this);
        Account[] accounts = ac.getAccountsByType(AccountUtil.ACCOUNT_TYPE);
        if (accounts.length > 0){
            SyncService.FileSyncAdapter.requestSync(path, accounts[0]);
        }
    }

    /**
     * Show notification for update changes task.
     */
    @SuppressWarnings("unused")
    @Deprecated
    private void showNotifyForTask(int taskId){
        Task task = Api.getTaskStateHandler().getTask(taskId);
        if (task == null) return;

        String title = getString(R.string.prompt_notify_upload_changes_for, "file name");

        TaskProgressNotifier notifier = new TaskProgressNotifier(
                new TaskProgressNotifier.StringTitle(title),
                task,
                this
        );
        task.addTaskListener(notifier);
        notifier.startListenProgress(true);
    }

    private String getEventDescription(int event){
        switch (event){
            case FileObserver.ATTRIB:
                return "ATTRIB";
            case FileObserver.CLOSE_WRITE:
                return "CLOSE_WRITE";
            case FileObserver.CREATE:
                return "CREATE";

            default:
                return "untracked";
        }
    }

    ////////////////////////////////////////////////
    // [START Classes] // <editor-fold desc="Classes">
    ////////////////////////////////////////////////

    private class LocalBinder extends Binder{

    }

    ////////////////////////////////////////////////
    // [END Classes] // </editor-fold>
    ////////////////////////////////////////////////
}
