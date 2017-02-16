package com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.presenter;

import android.os.Handler;
import android.os.Looper;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.interactor.FileObserverInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverView;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileObserverPresenterImpl extends BasePresenter<FileObserverView> implements FileObserverPresenter {

    private final FileObserverInteractor mInteractor;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mDelayedSync;

    @Inject
    FileObserverPresenterImpl(ViewState<FileObserverView> viewState, FileObserverInteractor interactor) {
        super(viewState);
        mInteractor = interactor;
    }

    @Override
    protected void onViewStart() {
        super.onViewStart();
        mInteractor.observeOfflineFilesChanges()
                .compose(this::untilStopView)
                .flatMap(this::checkOffline)
                .doOnError(this::onErrorObtained)
                .onErrorResumeNext(Observable.empty())
                .subscribe(this::handleChangedFile);
    }

    private Observable<AuroraFile> checkOffline(File file){
        return mInteractor.getOfflineFile(file)
                .toObservable()
                .switchIfEmpty(mInteractor.delete(file).toObservable());
    }

    private void handleChangedFile(AuroraFile file){
        if (mDelayedSync != null){
            mHandler.removeCallbacks(mDelayedSync);
        }
        mDelayedSync = () -> {
            mDelayedSync = null;
            mInteractor.requestSync().subscribe(() -> {}, this::onErrorObtained);
        };
        mHandler.postDelayed(mDelayedSync, 3000);
    }
}
