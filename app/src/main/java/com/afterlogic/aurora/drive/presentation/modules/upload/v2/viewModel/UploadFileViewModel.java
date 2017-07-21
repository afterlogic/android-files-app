package com.afterlogic.aurora.drive.presentation.modules.upload.v2.viewModel;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.AuroraFileViewModel;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFileViewModel extends AuroraFileViewModel {

    public UploadFileViewModel(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        super(file, onItemClickListener);
    }
}
