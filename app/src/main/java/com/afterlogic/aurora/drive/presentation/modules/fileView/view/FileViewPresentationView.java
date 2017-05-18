package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.view.LoadView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewPresentationView extends LoadView, PresentationView{

    void showRenameDialog(AuroraFile file, Consumer<String> newNameConsumer);
}
