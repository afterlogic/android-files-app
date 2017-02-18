package com.afterlogic.aurora.drive.presentation.modules.fileView.presenter;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.LoadPresenter;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewPresenter extends LoadPresenter {

    void setModel(FileViewModel model);

    void updateOffline(AuroraFile file);

    void onDelete();

    void onRename();

    void onOffline();

    void onDownload();

    void onSend();
}
