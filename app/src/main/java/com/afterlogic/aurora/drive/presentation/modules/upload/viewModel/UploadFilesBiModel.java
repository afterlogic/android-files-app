package com.afterlogic.aurora.drive.presentation.modules.upload.viewModel;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListBiModel;
import com.afterlogic.aurora.drive.presentation.modules.upload.model.presenter.UploadFilesPresenter;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class UploadFilesBiModel extends BaseFilesListBiModel<UploadFileItemViewModel> implements UploadFilesViewModel, UploadFilesModel {

    private final Provider<UploadFileItemBiModel> mItemModelProvider;

    @Inject UploadFilesBiModel(OptWeakRef<UploadFilesPresenter> presenter, Provider<UploadFileItemBiModel> itemModelProvider) {
        super(presenter);
        mItemModelProvider = itemModelProvider;
    }

    @Override
    protected UploadFileItemViewModel viewModel(AuroraFile file) {
        UploadFileItemBiModel model = mItemModelProvider.get();
        model.setAuroraFile(file);
        return model;
    }
}
