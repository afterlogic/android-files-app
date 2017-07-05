package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileViewModel extends BaseObservable {

    private final AuroraFile file;
    private final OnItemClickListener<AuroraFile> onItemClickListener;

    public ReplaceFileViewModel(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
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
