package com.afterlogic.aurora.drive.presentation.modules.main.model.presenter;

import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.FilesListPresenter;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFileListPresenter extends FilesListPresenter, OnBackPressedListener {

    void onDownload();

    void onSendTo();

    void onRename();

    void onToggleOffline();

    void onDelete();

    void onCreateFolder();

    void onFileUpload();

    void onMultiChoseMode(boolean multiChoiseMode);

    void onTogglePublicLink();

    void onCopyPublicLink();

    void onReplaceAction();

    void onCopyAction();
}
