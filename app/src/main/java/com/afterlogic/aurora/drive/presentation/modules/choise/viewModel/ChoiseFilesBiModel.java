package com.afterlogic.aurora.drive.presentation.modules.choise.viewModel;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesListBiModel;
import com.afterlogic.aurora.drive.presentation.modules.choise.model.presenter.ChoiseFilesPresenter;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class ChoiseFilesBiModel extends BaseFilesListBiModel<ChoiseFileItemViewModel> implements ChoiseFilesViewModel, ChoiseFilesModel {

    private final Provider<ChoiseFileItemBiModel> mItemModelProvider;

    @Inject
    ChoiseFilesBiModel(OptWeakRef<ChoiseFilesPresenter> presenter, Provider<ChoiseFileItemBiModel> itemModelProvider) {
        super(presenter);
        mItemModelProvider = itemModelProvider;
    }

    @Override
    protected ChoiseFileItemViewModel viewModel(AuroraFile file) {
        ChoiseFileItemBiModel model = mItemModelProvider.get();
        model.setAuroraFile(file);
        return model;
    }
}
