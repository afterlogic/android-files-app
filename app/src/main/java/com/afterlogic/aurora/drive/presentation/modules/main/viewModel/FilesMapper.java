package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

class FilesMapper {

    private final AppResources appResources;

    private final Map<AuroraFile, MainFileViewModel> byFileMap = new HashMap<>();
    private final Map<String, MainFileViewModel> byFileSpecMap = new HashMap<>();

    private OnActionListener<AuroraFile> onLongClickListener;

    @Inject
    FilesMapper(AppResources appResources) {
        this.appResources = appResources;
    }

    void setOnLongClickListener(OnActionListener<AuroraFile> onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    MainFileViewModel mapAndStore(AuroraFile file, OnActionListener<AuroraFile> onItemClickListener) {
        MainFileViewModel vm = new MainFileViewModel(file, onItemClickListener, onLongClickListener, appResources);
        byFileMap.put(file, vm);
        byFileSpecMap.put(file.getPathSpec(), vm);
        return vm;
    }

    @Nullable
    MainFileViewModel get(AuroraFile file) {
        return byFileMap.get(file);
    }

    @Nullable
    MainFileViewModel get(String fileSpec) {
        return byFileSpecMap.get(fileSpec);
    }

    @Nullable
    MainFileViewModel remove(AuroraFile file) {
        byFileSpecMap.remove(file.getPathSpec());
        return byFileMap.remove(file);
    }

    Set<AuroraFile> getKeys() {
        return byFileMap.keySet();
    }

    void clear() {
        byFileMap.clear();
        byFileSpecMap.clear();
    }
}
