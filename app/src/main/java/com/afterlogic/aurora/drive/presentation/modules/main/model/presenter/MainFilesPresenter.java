package com.afterlogic.aurora.drive.presentation.modules.main.model.presenter;

import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.FilesPresenter;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesPresenter extends FilesPresenter, OnBackPressedListener {
    void onLogout();
    void onMultiChoiseAction();
    void onOfflineModeSelected();
}
