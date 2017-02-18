package com.afterlogic.aurora.drive.presentation.modules.fileView.presenter;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewImageItemModel {
    void setUri(Uri uri);
    void setError();
    void setAuroraFile(AuroraFile file);

    AuroraFile getFile();
}
