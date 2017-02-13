package com.afterlogic.aurora.drive.presentation.modules.choise.viewModel;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemBiModel;
import com.afterlogic.aurora.drive.presentation.modules.choise.presenter.ChoiseFilesPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseFileItemBiModel extends BaseFileItemBiModel implements ChoiseFileItemViewModel, ChoiseFileItemModel {

    @Inject
    ChoiseFileItemBiModel(OptWeakRef<ChoiseFilesPresenter> presenter, AppResources appResources) {
        super(presenter, appResources);
    }
}
