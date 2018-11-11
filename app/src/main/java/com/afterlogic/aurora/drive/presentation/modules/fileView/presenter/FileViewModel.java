package com.afterlogic.aurora.drive.presentation.modules.fileView.presenter;

import androidx.annotation.Nullable;

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

    void remove(AuroraFile file);
    void rename(AuroraFile file, String name);
    void setOffline(AuroraFile file, boolean offline);
}
