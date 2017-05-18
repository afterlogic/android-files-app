package com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.assembly;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModule;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.interactor.FileObserverInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.interactor.FileObserverInteractorImpl;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.presenter.FileObserverPresenterImpl;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.presenter.FileObserverPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class FileObserverModule extends PresentationModule<FileObserverView> {

    @Provides
    FileObserverPresenter presenter(FileObserverPresenterImpl presenter){
        return presenter;
    }

    @Provides
    FileObserverInteractor interactor(FileObserverInteractorImpl interactor){
        return interactor;
    }
}
