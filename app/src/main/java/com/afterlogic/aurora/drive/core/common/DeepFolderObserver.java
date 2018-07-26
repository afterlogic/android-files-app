package com.afterlogic.aurora.drive.core.common;

import android.os.FileObserver;
import androidx.annotation.NonNull;
import android.util.Log;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sashka on 19.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class DeepFolderObserver{

    private static final String TAG = DeepFolderObserver.class.getSimpleName();

    private File mFolder;
    private HashMap<String, FileObserver> mFolderObservers = new HashMap<>();
    private FileEventListener mFileEventListener;
    private boolean mObserving;

    private int mEventMask;

    public DeepFolderObserver(File folder, int eventMask, @NonNull FileEventListener fileEventListener) {
        mFolder = folder;
        if (!mFolder.isDirectory()) {
            throw new IllegalArgumentException("File must be folder!");
        }
        mFileEventListener = fileEventListener;
        mEventMask = eventMask;
    }

    public synchronized void startObserve() {
        if (mObserving) return;
        mObserving = true;

        observe(mFolder);
    }

    public synchronized void stopObserve() {
        if (!mObserving) return;

        mObserving = false;
        for (Map.Entry<String, FileObserver> observer : mFolderObservers.entrySet()) {
            observer.getValue().stopWatching();
        }
        mFolderObservers.clear();
    }

    public boolean isObserving() {
        return mObserving;
    }

    private void observe(File folder) {
        if (folder.isDirectory()) {
            String path = folder.getPath();
            int realMask = mEventMask | FileObserver.DELETE_SELF | FileObserver.CREATE;
            FileObserver observer = new ExtFileObserver(path, realMask, this::onEvent);
            mFolderObservers.put(path, observer);

            if (mObserving) {
                observer.startWatching();
            }

            for (File deeperFolder : folder.listFiles(File::isDirectory)) {
                observe(deeperFolder);
            }
        }
    }

    public void onEvent(int event, String path) {
        switch (event & FileObserver.ALL_EVENTS) {
            case FileObserver.DELETE_SELF:
                Log.d(TAG, "Delete observer observers: " + path);
                FileObserver observer = mFolderObservers.get(path);
                if (observer != null) {
                    observer.stopWatching();
                }
                mFolderObservers.remove(path);
                break;
            case FileObserver.CREATE:
                File newFile = new File(path);
                if (newFile.isDirectory()) {
                    Log.d(TAG, "Add created to observers: " + path);
                    observe(newFile);
                }
                break;
            default:
        }

        if ((event & mEventMask) != 0){
            mFileEventListener.onEvent(event, path);
        }
    }

    /**
     * Created by sashka on 19.04.16.
     * mail: sunnyday.development@gmail.com
     */
    private static class ExtFileObserver extends FileObserver {

        private FileEventListener mFileEventListener;
        private String mFolder;

        @SuppressWarnings("WeakerAccess")
        public ExtFileObserver(String folder, int mask, FileEventListener fileEventListener) {
            super(folder, mask);
            mFileEventListener = fileEventListener;
            mFolder = folder;
        }

        @Override
        public void onEvent(int event, String path) {
            MyLog.d("raw event " + event + ": " + path);
            path = path == null ? mFolder : mFolder + File.separator + path;
            mFileEventListener.onEvent(event & FileObserver.ALL_EVENTS, path);
        }

        @Override
        public void startWatching() {
            MyLog.d("Start watching: " + mFolder);
            super.startWatching();
        }

        @Override
        public void stopWatching() {
            MyLog.d("Stop watching: " + mFolder);
            super.stopWatching();
        }
    }

    /**
     * Created by sashka on 19.04.16.
     * mail: sunnyday.development@gmail.com
     */
    public interface FileEventListener {
        void onEvent(int event, String path);
    }
}
