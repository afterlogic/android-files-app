package com.afterlogic.aurora.drive.presentation.modules.filelist.view;

import android.support.annotation.FloatRange;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.Repeat;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.RepeatPolicy;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileListView extends PresentationView {

    @Repeat(group = "progress", value = RepeatPolicy.LAST)
    void showDownloadProgress(String fileName, @FloatRange(from = -1, to = 100) float progress);

    @Repeat(group = "progress")
    void hideProgress();

    void showFileActions(AuroraFile file);

    void showRenameDialog(AuroraFile file, Consumer<String> newNameConsumer);

    @Repeat(group = "progress", value = RepeatPolicy.LAST)
    void showFileDeletingProgress(String fileName);

    @Repeat(group = "progress", value = RepeatPolicy.LAST)
    void showFileRenamingProgress(String fileName);
}
