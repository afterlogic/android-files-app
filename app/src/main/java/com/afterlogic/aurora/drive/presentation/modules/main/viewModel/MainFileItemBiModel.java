package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;

import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemBiModel;
import com.afterlogic.aurora.drive.presentation.modules.main.presenter.MainFileListPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFileItemBiModel extends BaseFileItemBiModel implements MainFileItemViewModel, MainFileItemModel{

    private final ObservableBoolean mSelected = new ObservableBoolean();

    @Inject
    MainFileItemBiModel(OptWeakRef<MainFileListPresenter> presenter,
                               AppResources appResources) {
        super(presenter, appResources);
    }

    @Override
    public ObservableBoolean getSelected() {
        return mSelected;
    }

}
