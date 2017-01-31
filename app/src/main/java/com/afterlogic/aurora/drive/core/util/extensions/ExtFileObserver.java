package com.afterlogic.aurora.drive.core.util.extensions;

import android.os.FileObserver;
import android.util.Log;

import com.afterlogic.aurora.drive.core.util.interfaces.FileEventListener;

import java.io.File;

/**
 * Created by sashka on 19.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class ExtFileObserver extends FileObserver {

    public static final String TAG = ExtFileObserver.class.getSimpleName();

    private FileEventListener mFileEventListener;
    private String mFolder;

    public ExtFileObserver(String path, FileEventListener fileEventListener) {
        super(path);
        mFileEventListener = fileEventListener;
        mFolder = path;
    }

    public ExtFileObserver(String path, int mask, FileEventListener fileEventListener) {
        super(path, mask);
        mFileEventListener = fileEventListener;
        mFolder = path;
    }

    @Override
    public void onEvent(int event, String path) {
        Log.d(TAG, "raw event " + event + ": " + path);
        path = path == null ? mFolder : mFolder + File.separator + path;
        mFileEventListener.onEvent(event & FileObserver.ALL_EVENTS, path);
    }

    @Override
    public void startWatching() {
        Log.d(TAG, "Start watching: " + mFolder);
        super.startWatching();
    }

    @Override
    public void stopWatching() {
        Log.d(TAG, "Start watching: " + mFolder);
        super.stopWatching();
    }
}
