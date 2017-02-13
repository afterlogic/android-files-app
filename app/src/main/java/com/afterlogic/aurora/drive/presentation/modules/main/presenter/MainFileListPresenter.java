package com.afterlogic.aurora.drive.presentation.modules.main.presenter;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter.FilesListPresenter;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFileListPresenter extends FilesListPresenter, OnBackPressedListener {

    void onDownload(@Nullable AuroraFile file);

    void onSendTo(@Nullable AuroraFile file);

    void onRename(AuroraFile file);

    void onToggleOffline(@Nullable AuroraFile file);

    void onDelete(@Nullable AuroraFile file);

    void onCreateFolder();

    void onFileUpload();

    void onMultiChoseMode(boolean multiChoiseMode);
}
