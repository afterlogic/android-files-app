package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceFileTypeArgs;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeViewModel extends BaseViewModel {

    private String fileType;

    @Inject
    public ReplaceFileTypeViewModel() {
    }

    public void setArgs(ReplaceFileTypeArgs args) {
        fileType = args.getType();
    }
}
