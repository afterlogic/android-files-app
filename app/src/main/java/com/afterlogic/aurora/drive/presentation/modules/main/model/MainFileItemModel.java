package com.afterlogic.aurora.drive.presentation.modules.main.model;

import android.support.annotation.IntRange;

import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.BaseFileItemModel;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFileItemModel extends BaseFileItemModel{
    void setOffline(boolean offline);
    void setSyncProgress(@IntRange(from = -1, to = 100) int progress);
}
