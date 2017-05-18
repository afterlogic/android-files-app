package com.afterlogic.aurora.drive.presentation.modules.upload.viewModel;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemBiModel;
import com.afterlogic.aurora.drive.presentation.modules.upload.model.presenter.UploadFilesPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFileItemBiModel extends BaseFileItemBiModel implements UploadFileItemViewModel, UploadFileItemModel {

    @Inject UploadFileItemBiModel(OptWeakRef<UploadFilesPresenter> presenter, AppResources appResources) {
        super(presenter, appResources);
    }
}
