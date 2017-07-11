package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

class FilesMapper {

    private final AppResources appResources;

    private final Map<AuroraFile, MainFileViewModel> filesMap = new HashMap<>();

    @Inject
    FilesMapper(AppResources appResources) {
        this.appResources = appResources;
    }

    public MainFileViewModel map(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        MainFileViewModel vm = new MainFileViewModel(file, onItemClickListener, appResources);
        filesMap.put(file, vm);
        return vm;
    }

    @Nullable
    public MainFileViewModel get(AuroraFile file) {
        return filesMap.get(file);
    }

    void clear() {
        filesMap.clear();
    }
}
