package com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.model.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.interactor.FilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesModel;

import java.util.Collections;

import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class BaseFilesPresenter<V extends PresentationView> extends BasePresenter<V> implements FilesPresenter {

    private final FilesInteractor mInteractor;
    private final BaseFilesModel mModel;

    private Disposable mCurrentRefresh = null;

    public BaseFilesPresenter(ViewState<V> viewState, FilesInteractor interactor, BaseFilesModel model) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        refresh();
    }

    @Override
    public void onCurrentFolderChanged(AuroraFile folder) {
        mModel.setCurrentFolder(folder);
    }

    @Override
    public void refresh() {
        if (mCurrentRefresh != null){
            mCurrentRefresh.dispose();
        }

        mCurrentRefresh = mInteractor.getAvailableFileTypes()
                .doOnSubscribe(disposable -> {
                    mModel.setFileTypes(Collections.emptyList());
                    mModel.setRefreshing(true);
                })
                .doFinally(() -> {
                    mCurrentRefresh = null;
                    mModel.setRefreshing(false);
                })
                .subscribe(
                        mModel::setFileTypes,
                        this::onErrorObtained
                );
    }
}
