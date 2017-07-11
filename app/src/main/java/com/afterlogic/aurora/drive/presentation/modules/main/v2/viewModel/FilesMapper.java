package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

class FilesMapper {

    private final AppResources appResources;

    @Inject
    FilesMapper(AppResources appResources) {
        this.appResources = appResources;
    }

    public MainFileViewModel map(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return new MainFileViewModel(file, onItemClickListener, appResources);
    }
}
