package com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileModel {
    void setThumbNail(Uri uri);
    AuroraFile getFile();
    void updateBy(AuroraFile file);
}
