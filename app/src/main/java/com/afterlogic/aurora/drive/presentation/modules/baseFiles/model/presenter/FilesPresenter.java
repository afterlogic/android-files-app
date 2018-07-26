package com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.presenter;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.model.presenter.Presenter;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FilesPresenter extends Presenter{

    void onCurrentFolderChanged(AuroraFile folder);

    void refresh();
}
