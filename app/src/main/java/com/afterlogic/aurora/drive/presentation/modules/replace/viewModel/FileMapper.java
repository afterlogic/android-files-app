package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class FileMapper implements Mapper<ReplaceFileViewModel, AuroraFile> {

    private final OnItemClickListener<AuroraFile> onItemClickListener;

    FileMapper(OnItemClickListener<AuroraFile> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ReplaceFileViewModel map(AuroraFile source) {
        return new ReplaceFileViewModel(source, onItemClickListener);
    }
}
