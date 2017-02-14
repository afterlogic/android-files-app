package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view;

import android.support.annotation.FloatRange;

import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface SyncView extends PresentationView {
    void notifyProgress(String fileName, @FloatRange(from = -1, to = 100) float progress);

    void hideProgressNotify();
}
