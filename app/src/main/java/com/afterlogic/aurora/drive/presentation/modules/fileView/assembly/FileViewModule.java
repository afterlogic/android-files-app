package com.afterlogic.aurora.drive.presentation.modules.fileView.assembly;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileViewPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewPresentationView;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewBiModel;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewModel;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class FileViewModule extends PresentationModule<FileViewPresentationView>{

    @Provides
    FileViewModel model(FileViewBiModel model){
        return model;
    }

    @Provides
    FileViewViewModel viewModel(FileViewBiModel model){
        return model;
    }

    @Provides
    FileViewPresenter presenter(FileViewPresenterImpl presenter){
        return presenter;
    }
}
