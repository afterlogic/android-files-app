package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewModel {
    void setItems(List<AuroraFile> fileList);
    @Nullable
    AuroraFile getCurrent();
}
