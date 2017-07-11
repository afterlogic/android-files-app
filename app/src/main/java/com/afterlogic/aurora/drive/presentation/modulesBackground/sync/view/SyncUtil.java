package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class SyncUtil {

    static Bundle map(SyncProgress progress){
        Bundle bundle = new Bundle();
        bundle.putString("file", progress.getFilePathSpec());
        bundle.putString("filename", progress.getFileName());
        bundle.putInt("progress", progress.getProgress());
        bundle.putBoolean("done", progress.isDone());
        return bundle;
    }

    static SyncProgress map(Bundle bundle){
        return new SyncProgress(
                bundle.getString("file"),
                bundle.getString("filename"),
                bundle.getInt("progress"),
                bundle.getBoolean("done")
        );
    }
}
