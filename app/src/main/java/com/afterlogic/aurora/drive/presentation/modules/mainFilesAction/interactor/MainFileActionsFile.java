package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFileActionsFile {

    private final AuroraFile file;

    private final ObservableField<Uri> icon;

    private final ObservableBoolean offline;

    public MainFileActionsFile(AuroraFile file,
                               ObservableField<Uri> icon,
                               ObservableBoolean offline) {
        this.file = file;
        this.icon = icon;
        this.offline = offline;
    }

    public AuroraFile getFile() {
        return file;
    }

    public ObservableField<Uri> getIcon() {
        return icon;
    }

    public String getTitle() {
        return file.getName();
    }

    public ObservableBoolean getOffline() {
        return offline;
    }

}
