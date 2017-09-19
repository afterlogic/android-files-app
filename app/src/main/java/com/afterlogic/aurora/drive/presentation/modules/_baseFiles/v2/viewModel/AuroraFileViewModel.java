package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class AuroraFileViewModel extends BaseObservable {

    private final AuroraFile file;
    private final OnActionListener<AuroraFile> onItemClickListener;

    public AuroraFileViewModel(AuroraFile file, OnActionListener<AuroraFile> onItemClickListener) {
        this.file = file;
        this.onItemClickListener = onItemClickListener;
    }

    @Bindable
    public String getFileName() {
        return file.getName();
    }

    public void onClick() {
        onItemClickListener.onAction(file);
    }
}
