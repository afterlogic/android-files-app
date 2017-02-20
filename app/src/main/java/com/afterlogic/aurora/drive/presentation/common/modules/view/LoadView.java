package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.support.annotation.FloatRange;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.Repeat;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.RepeatPolicy;

/**
 * Created by sashka on 18.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface LoadView extends PresentationView{
    @Repeat(group = "filelist_progress", value = RepeatPolicy.LAST)
    void showLoadProgress(String fileName, String title, @FloatRange(from = -1, to = 100) float progress);

    @Repeat(group = "filelist_progress", value = RepeatPolicy.LAST)
    void showProgress(String title, String message);

    @Repeat(group = "filelist_progress")
    void hideProgress();
}
