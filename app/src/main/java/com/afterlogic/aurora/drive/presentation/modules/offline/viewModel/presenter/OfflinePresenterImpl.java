package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.presenter;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.util.DisposeBag;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.interactor.OfflineInteractor;
import com.afterlogic.aurora.drive.presentation.modules.offline.model.router.OfflineRouter;
import com.annimon.stream.Stream;

import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflinePresenterImpl implements OfflinePresenter {

    private final OfflineInteractor mInteractor;
    private final OfflineModelOutput mModel;
    private final OfflineRouter mRouter;

    private final ObservableScheduler mScheduler;

    private Disposable mCurrentLoad;
    private DisposeBag mActiveDisposeBag = new DisposeBag();

    @Inject
    OfflinePresenterImpl(OfflineInteractor interactor, OfflineModelOutput model, OfflineRouter router, ObservableScheduler scheduler) {
        mInteractor = interactor;
        mModel = model;
        mRouter = router;
        mScheduler = scheduler;
    }

    @Override
    public void checkAuth() {
        mInteractor.getAuthStatus()
                .subscribe(
                        auth -> {
                            if (!auth){
                                mRouter.openAuth();
                            }
                        },
                        mModel::onErrorObtained
                );
    }

    @Override
    public void refresh() {
        mInteractor.getOfflineFiles()
                .doOnSubscribe(disposable -> mModel.notifyRefreshing(true))
                .doFinally(() -> mModel.notifyRefreshing(false))
                .compose(mScheduler::defaultSchedulers)
                .subscribe(
                        files -> {
                            mModel.setFiles(files);
                            updateThumbs(files);
                        },
                        mModel::onErrorObtained
                );
    }

    @Override
    public void downloadFile(AuroraFile file) {
        mInteractor.downloadToDownloads(file)
                .compose(loadTask(file))
                .subscribe(
                        localFile -> mRouter.openFile(file, localFile),
                        mModel::onErrorObtained
                );
    }

    @Override
    public void openFile(AuroraFile file) {
        if (!mRouter.canOpen(file)){
            mModel.onCantOpenFile(file);
            return;
        }

        mInteractor.downloadForOpen(file)
                .compose(loadTask(file))
                .subscribe(
                        localFile -> mRouter.openFile(file, localFile),
                        mModel::onErrorObtained
                );
    }

    @Override
    public void sendFile(AuroraFile file) {

    }

    @Override
    public void cancelCurrentLoad() {
        if (mCurrentLoad != null){
            mCurrentLoad.dispose();
            mCurrentLoad = null;
        }
    }

    @Override
    public void onGoToOnline() {
        mRouter.goToOnline();
    }

    @Override
    public void onViewStart() {
        mInteractor.listenNetworkState()
                .compose(mActiveDisposeBag::add)
                .subscribe(
                        mModel::handleNetworkState,
                        mModel::onErrorObtained
                );
    }

    @Override
    public void onViewStop() {
        mActiveDisposeBag.dispose();
    }

    private void updateThumbs(List<AuroraFile> files){
        Stream.of(files)
                .filter(AuroraFile::isPreviewAble)
                .map(file -> mInteractor.getThumbnail(file)
                        .doOnSuccess(uri -> mModel.setThumb(file, uri))
                        .toCompletable()
                        .doOnError(mModel::onErrorObtained)
                        .onErrorComplete()
                )
                .collect(Observables.Collectors.concatCompletable())
                .compose(mScheduler::defaultSchedulers)
                .subscribe(() -> {}, mModel::onErrorObtained);
    }

    private <T>ObservableTransformer<Progressible<T>, T> loadTask(AuroraFile file){
        return observable -> observable.doOnSubscribe(disposable -> mCurrentLoad = disposable)
                .doFinally(() -> mCurrentLoad = null)
                .startWith(new Progressible<>(null, 0, 0, file.getName(), false))
                .doOnNext(progress -> {
                    float percent = progress.getMax() > 0 && progress.getProgress() >= 0 ?
                            ((float)progress.getProgress() / progress.getMax()) * 100 : -1;
                    mModel.notifyLoadProgress(file, 100, (int)percent);
                })
                .doFinally(mModel::notifyLoadFinished)
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .onErrorResumeNext(error -> {
                    if (error instanceof FileNotFoundException){
                        mModel.onFileLoadError(file);
                        return Observable.empty();
                    } else {
                        return Observable.error(error);
                    }
                })
                .compose(mScheduler::defaultSchedulers);
    }
}
