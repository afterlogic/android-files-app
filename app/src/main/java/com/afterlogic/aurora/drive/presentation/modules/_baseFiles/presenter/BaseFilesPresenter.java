package com.afterlogic.aurora.drive.presentation.modules._baseFiles.presenter;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.FilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesModel;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesPresenter<V extends PresentationView> extends BasePresenter<V> implements FilesPresenter {

    private final FilesInteractor mInteractor;
    private final BaseFilesModel mModel;

    public BaseFilesPresenter(ViewState<V> viewState, FilesInteractor interactor, BaseFilesModel model) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        mInteractor.getAvailableFileTypes()
                .subscribe(
                        mModel::setFileTypes,
                        this::onErrorObtained
                );
    }

    @Override
    public void onCurrentFolderChanged(AuroraFile folder) {
        mModel.setCurrentFolder(folder);
    }
}
