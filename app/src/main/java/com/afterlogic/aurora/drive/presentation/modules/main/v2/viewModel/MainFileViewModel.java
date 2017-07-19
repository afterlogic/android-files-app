package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.BaseAuroraFileViewModel;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFileViewModel extends BaseAuroraFileViewModel {

    MainFileViewModel(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        super(file, onItemClickListener);
    }
}
