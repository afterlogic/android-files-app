package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.BaseAuroraFileViewModel;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileViewModel extends BaseAuroraFileViewModel {

    public ReplaceFileViewModel(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        super(file, onItemClickListener);
    }
}
