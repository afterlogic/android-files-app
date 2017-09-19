package com.afterlogic.aurora.drive.presentation.modules.upload.viewModel;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunny on 19.09.17.
 * mail: mail@sunnydaydev.me
 */

class FileMap {

    private final AppResources appResources;

    private final Map<AuroraFile, UploadFileViewModel> byValue = new HashMap<>();

    FileMap(AppResources appResources) {
        this.appResources = appResources;
    }

    public UploadFileViewModel mapAndStoreSource(AuroraFile file, OnActionListener<AuroraFile> onClick) {

        UploadFileViewModel vm = new UploadFileViewModel(file, onClick, appResources);

        byValue.put(file, vm);

        return vm;

    }

    @Nullable
    public UploadFileViewModel getViewModel(AuroraFile file) {

        return byValue.get(file);

    }

    public void clear() {

        byValue.clear();

    }

}
