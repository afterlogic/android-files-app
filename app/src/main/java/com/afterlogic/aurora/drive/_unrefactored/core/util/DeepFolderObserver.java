package com.afterlogic.aurora.drive._unrefactored.core.util;

import android.os.FileObserver;
import android.util.Log;

import com.afterlogic.aurora.drive._unrefactored.core.util.extensions.ExtFileObserver;
import com.afterlogic.aurora.drive._unrefactored.core.util.interfaces.FileEventListener;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sashka on 19.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class DeepFolderObserver implements FileEventListener {

    public static final String TAG = DeepFolderObserver.class.getSimpleName();

    private static final FileFilter DIRECTORY_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    private File mFolder;
    private HashMap<String, FileObserver> mFolderObservers = new HashMap<>();
    private FileEventListener mFileEventListener;
    private boolean mIsListening;

    private int mEventMask;

    public DeepFolderObserver(File folder, int eventMask, FileEventListener fileEventListener) {
        mFolder = folder;
        if (!mFolder.isDirectory()) {
            throw new IllegalArgumentException("File must be folder!");
        }
        mFileEventListener = fileEventListener;
        mEventMask = eventMask;
    }

    public void startWatching() {
        if (mIsListening) return;

        mIsListening = true;
        mFolderObservers.clear();
        observe(mFolder);
    }

    public void stopWatching() {
        if (!mIsListening) return;

        mIsListening = false;
        for (Map.Entry<String, FileObserver> observer : mFolderObservers.entrySet()) {
            observer.getValue().stopWatching();
        }
        mFolderObservers.clear();
    }

    private void observe(File folder) {
        if (folder.isDirectory()) {
            String path = folder.getAbsolutePath();
            int realMask = mEventMask | FileObserver.DELETE_SELF | FileObserver.CREATE;
            FileObserver observer = new ExtFileObserver(path, realMask, this);
            mFolderObservers.put(path, observer);

            if (mIsListening) {
                observer.startWatching();
            }

            for (File deeperFolder : folder.listFiles(DIRECTORY_FILTER)) {
                observe(deeperFolder);
            }
        }
    }

    @Override
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
        sendEventToListener(event, path);
    }

    private void sendEventToListener(int event, String path){
        if ((event & mEventMask) != 0){
            mFileEventListener.onEvent(event, path);
        }
    }
}
