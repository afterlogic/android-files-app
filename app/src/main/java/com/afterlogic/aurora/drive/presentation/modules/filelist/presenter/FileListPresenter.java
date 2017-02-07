package com.afterlogic.aurora.drive.presentation.modules.filelist.presenter;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.Presenter;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesCallback;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileListPresenter extends Presenter, OnBackPressedListener {

    void initWith(String type, MainFilesCallback callback);

    void onRefresh();

    void onFileClick(AuroraFile file);

    void onFileLongClick(AuroraFile file);
}
