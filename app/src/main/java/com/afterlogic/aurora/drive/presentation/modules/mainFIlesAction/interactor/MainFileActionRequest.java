package com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFileActionRequest {

    private final AuroraFile file;

    private final ObservableField<Uri> icon;

    private final ObservableBoolean offline;

    private final MainFileActionCallback callback;

    public MainFileActionRequest(AuroraFile file,
                                 ObservableField<Uri> icon,
                                 ObservableBoolean offline,
                                 MainFileActionCallback callback) {
        this.file = file;
        this.icon = icon;
        this.offline = offline;
        this.callback = callback;
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

    public MainFileActionCallback getCallback() {
        return callback;
    }
}
