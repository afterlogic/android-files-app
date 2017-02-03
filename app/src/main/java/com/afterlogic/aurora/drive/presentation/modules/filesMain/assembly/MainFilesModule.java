package com.afterlogic.aurora.drive.presentation.modules.filesMain.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.presenter.MainFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.presenter.MainFilesPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesView;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.MainFilesModel;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.MainFilesViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class MainFilesModule extends PresentationModule<MainFilesView> {

    @Provides @ModuleScope
    MainFilesPresenter presenter(MainFilesPresenterImpl presenter){
        return presenter;
    }

    @Provides @ModuleScope
    MainFilesViewModel viewModel(){
        return new MainFilesViewModel();
    }

    @Provides
    MainFilesModel model(MainFilesViewModel model){
        return model.getController();
    }

}
