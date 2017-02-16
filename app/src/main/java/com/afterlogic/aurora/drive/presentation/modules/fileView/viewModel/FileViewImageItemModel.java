package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewImageItemModel {
    void setProgress(boolean progress);
    void setUri(Uri uri);
    void setError();

    AuroraFile getFile();
}
