package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class BaseAuroraFileViewModel extends BaseObservable {

    private final AuroraFile file;
    private final OnItemClickListener<AuroraFile> onItemClickListener;

    public BaseAuroraFileViewModel(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        this.file = file;
        this.onItemClickListener = onItemClickListener;
    }

    @Bindable
    public String getFileName() {
        return file.getName();
    }

    public void onClick() {
        onItemClickListener.onItemClicked(-1, file);
    }
}
