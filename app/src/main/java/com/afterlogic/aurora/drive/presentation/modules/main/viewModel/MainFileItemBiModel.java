package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.databinding.ObservableBoolean;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.OfflineType;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFileItemBiModel;
import com.afterlogic.aurora.drive.presentation.modules.main.presenter.MainFileListPresenter;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFileItemBiModel extends BaseFileItemBiModel implements MainFileItemViewModel, MainFileItemModel{

    private final ObservableBoolean mSelected = new ObservableBoolean();
    private final AppResources mAppResources;

    @Inject
    MainFileItemBiModel(OptWeakRef<MainFileListPresenter> presenter,
                               AppResources appResources) {
        super(presenter, appResources);
        mAppResources = appResources;
    }

    @Override
    public void setAuroraFile(AuroraFile file) {
        super.setAuroraFile(file);
        if (file.getOfflineInfo().getOfflineType() == OfflineType.OFFLINE){
            getStatusIcon().set(mAppResources.getResourceUri(R.drawable.ic_offline));
        }
    }

    @Override
    public ObservableBoolean getSelected() {
        return mSelected;
    }

}
