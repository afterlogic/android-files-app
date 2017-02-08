package com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules.filelist.presenter.FileListPresenter;

import java.util.List;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileListModel {

    void setPresenter(FileListPresenter presenter);

    void setFileList(List<AuroraFile> files);

    void setRefreshing(boolean isRefreshing);

    void setThumbNail(AuroraFile file, Uri thumbUri);

    void changeFile(AuroraFile previous, AuroraFile newFile);

    void removeFile(AuroraFile file);

    List<AuroraFile> getFiles();
}
