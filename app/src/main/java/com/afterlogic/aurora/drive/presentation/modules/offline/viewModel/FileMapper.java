package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

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
        return new OfflineFileViewModel(auroraFile, onItemClickListener, appResources, id);
    }
}
