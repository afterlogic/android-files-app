package com.afterlogic.aurora.drive.presentation.modules.fileView.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.fileView.interactor.FileViewImageItemInteractor;
import com.afterlogic.aurora.drive.presentation.modules.fileView.interactor.FileViewImageItemInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileVIewImageItemPresenter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileViewImageItemPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewImageItemView;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemBiModel;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemModel;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class FileViewImageItemModule extends PresentationModule<FileViewImageItemView> {

    @Provides @SubModuleScope
    FileViewImageItemViewModel viewModel(FileViewImageItemBiModel model){
        return model;
    }

    @Provides @SubModuleScope
    FileViewImageItemModel model(FileViewImageItemBiModel model){
        return model;
    }

    @Provides
    FileVIewImageItemPresenter presenter(FileViewImageItemPresenterImpl presenter){
        return presenter;
    }

    @Provides
    FileViewImageItemInteractor interactor(FileViewImageItemInteractorImpl interactor){
        return interactor;
    }
}
