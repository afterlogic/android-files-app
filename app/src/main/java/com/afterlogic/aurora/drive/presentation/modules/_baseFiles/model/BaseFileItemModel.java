package com.afterlogic.aurora.drive.presentation.modules._baseFiles.model;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFileItemModel {
    void setThumbNail(Uri uri);
    void setAuroraFile(AuroraFile file);
    AuroraFile getFile();
}
