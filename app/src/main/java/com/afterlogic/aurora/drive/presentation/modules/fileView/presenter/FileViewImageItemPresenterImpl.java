package com.afterlogic.aurora.drive.presentation.modules.fileView.presenter;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules.fileView.interactor.FileViewImageItemInteractor;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewImageItemView;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemModel;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewImageItemPresenterImpl extends BasePresenter<FileViewImageItemView> implements FileVIewImageItemPresenter {

    private final FileViewImageItemInteractor mInteractor;
    private final FileViewImageItemModel mModel;

    @Inject FileViewImageItemPresenterImpl(ViewState<FileViewImageItemView> viewState, FileViewImageItemInteractor interactor, FileViewImageItemModel model) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
    }

    @Override
    protected void onPresenterStart() {
        super.onPresenterStart();
        mInteractor.donwloadToCache(mModel.getFile())
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .map(Uri::fromFile)
                .doOnError(error -> mModel.setError())
                .doOnSubscribe(disposable -> mModel.setProgress(true))
                .doOnComplete(() -> mModel.setProgress(false))
                .subscribe(
                        mModel::setUri,
                        this::onErrorObtained
                );
    }
}
