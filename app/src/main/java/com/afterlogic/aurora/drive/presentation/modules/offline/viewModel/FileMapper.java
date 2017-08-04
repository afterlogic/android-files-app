package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

class FileMapper {

    private final AppResources appResources;

    private long lastStableId = 1;
    private final Map<String, Long> stableIds = new HashMap<>();

    private final Map<String, OfflineFileViewModel> filesMap = new HashMap<>();

    @Inject
    FileMapper(AppResources appResources) {
        this.appResources = appResources;
    }

    public OfflineFileViewModel map(AuroraFile auroraFile, OnItemClickListener<AuroraFile> onItemClickListener) {
        String pathSpec = auroraFile.getPathSpec();
        long id;
        if (stableIds.containsKey(pathSpec)) {
            id = stableIds.get(pathSpec);
        } else {
            id = lastStableId++;
            stableIds.put(pathSpec, id);
        }
        OfflineFileViewModel vm = new OfflineFileViewModel(auroraFile, onItemClickListener, appResources, id);
        filesMap.put(auroraFile.getPathSpec(), vm);
        return vm;
    }

    public void clear() {
        filesMap.clear();
    }

    @Nullable
    public OfflineFileViewModel get(@NonNull AuroraFile file) {
        return filesMap.get(file.getPathSpec());
    }

    @Nullable
    public OfflineFileViewModel get(String pathSpec) {
        return filesMap.get(pathSpec);
    }
}
