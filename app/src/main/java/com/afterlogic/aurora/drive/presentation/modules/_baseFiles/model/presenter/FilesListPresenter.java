package com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.common.modules.model.presenter.LoadPresenter;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FilesListPresenter extends LoadPresenter, OnBackPressedListener {

    void initWith(String type);

    void onRefresh();

    void onFileClick(AuroraFile file);

    void onFileLongClick(AuroraFile file);
}
