package com.afterlogic.aurora.drive.presentation.modules.upload.presenter;

import android.net.Uri;

import com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter.FilesListPresenter;

import java.util.List;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface UploadFilesPresenter extends FilesListPresenter {
    void onCreateFolder();
    void onUpload(List<Uri> sources);
}
