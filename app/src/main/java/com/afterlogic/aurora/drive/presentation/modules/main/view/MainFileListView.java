package com.afterlogic.aurora.drive.presentation.modules.main.view;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesListView;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFileListView extends FilesListView {

    void showFileActions(AuroraFile file);

    void showRenameDialog(AuroraFile file, Consumer<String> newNameConsumer);

    void showNewFolderNameDialog(Consumer<String> newNameConsumer);

}
