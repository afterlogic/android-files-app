package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

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

    private OnFileViewLongClickListener onLongClickListener;

    @Inject
    FileMapper(AppResources appResources) {
        this.appResources = appResources;
    }

    OfflineFileViewModel map(AuroraFile auroraFile, OnItemClickListener<AuroraFile> onItemClickListener) {
        String pathSpec = auroraFile.getPathSpec();
        long id;
        if (stableIds.containsKey(pathSpec)) {
            id = stableIds.get(pathSpec);
        } else {
            id = lastStableId++;
            stableIds.put(pathSpec, id);
        }
        OfflineFileViewModel vm = new OfflineFileViewModel(auroraFile, onItemClickListener, this::onFileLongClick, appResources, id);
        filesMap.put(auroraFile.getPathSpec(), vm);
        return vm;
    }

    void clear() {
        filesMap.clear();
    }

    @Nullable
    OfflineFileViewModel get(@NonNull AuroraFile file) {
        return filesMap.get(file.getPathSpec());
    }

    @Nullable
    OfflineFileViewModel get(String pathSpec) {
        return filesMap.get(pathSpec);
    }

    void setOnLongClickListener(OnFileViewLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    private void onFileLongClick(View view, AuroraFile file) {
        if (onLongClickListener != null) {
            onLongClickListener.onFileLongLick(view, file);
        }
    }
}
