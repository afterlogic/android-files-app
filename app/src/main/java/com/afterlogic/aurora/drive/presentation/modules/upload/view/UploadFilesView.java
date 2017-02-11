package com.afterlogic.aurora.drive.presentation.modules.upload.view;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesListView;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface UploadFilesView extends FilesListView {
    void showNewFolderNameDialog(Consumer<String> nameConsumer);
}
