package com.afterlogic.aurora.drive.presentation.modules.fileView.presenter;

import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules.fileView.interactor.FileViewImageItemInteractor;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewImageItemPresenterImpl implements FileViewImageItemPresenter {

    private final FileViewImageItemInteractor mInteractor;
    private FileViewImageItemModel mModel;

    private Disposable mCurrentTask;

    @Inject FileViewImageItemPresenterImpl(FileViewImageItemInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void setModel(FileViewImageItemModel model) {
        mModel = model;
    }

    @Override
    public void onStart() {
        Observable.fromCallable(() -> mModel.getFile())
                .flatMap(mInteractor::donwloadToCache)
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .map(Uri::fromFile)
                .doOnError(error -> mModel.setError())
                .doOnSubscribe(disposable -> mCurrentTask = disposable)
                .doFinally(() -> mCurrentTask = null)
                .subscribe(
                        mModel::setUri,
                        MyLog::majorException
                );
    }

    @Override
    public void onStop() {
        if (mCurrentTask != null){
            mCurrentTask.dispose();
            mCurrentTask = null;
        }
    }
}
