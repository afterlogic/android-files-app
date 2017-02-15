package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.presenter;

import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.presenter.BasePresenter;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.interactor.SyncInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncView;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncModel;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SyncPresenterImpl extends BasePresenter<SyncView> implements SyncPresenter {

    private final SyncInteractor mInteractor;

    private final SyncModel mModel;

    @Inject SyncPresenterImpl(ViewState<SyncView> viewState, SyncInteractor interactor, SyncModel model) {
        super(viewState);
        mInteractor = interactor;
        mModel = model;
    }

    @Override
    public void onSyncPerformed() {
        Holder<Progressible<CheckPair>> last = new Holder<>();
        mInteractor.getOfflineFiles()
                .toObservable()
                .compose(Observables::forEach)
                .sorted((l, r) -> (int) (r.getLastModified() - l.getLastModified()))
                .flatMap(this::check)
                .filter(check -> check.getSyncType() != SyncType.NO_NEED)
                .flatMap(this::sync)
                .map(this::mapProgressToPercent)
                .filter(progress -> last.get() == null ||
                        last.get().getData() != progress.getData() ||
                        last.get().getProgress() != progress.getProgress()
                )
                .doOnNext(progress -> {
                    last.set(progress);

                    AuroraFile local = progress.getData().getLocal();
                    mModel.setCurrentSyncProgress(new SyncProgress(
                            local.getType() + local.getFullPath(),
                            local.getName(),
                            (int) progress.getProgress(),
                            progress.isDone()
                    ));
                })
                .ignoreElements()
                .doFinally(() -> mModel.setCurrentSyncProgress(null))
                .subscribe(
                        () -> {},
                        this::onErrorObtained
                );
    }

    private Observable<CheckPair> check(AuroraFile local){
        return mInteractor.check(local)
                .map(remote -> new CheckPair(local, remote))
                .map(check -> {
                    long localModified = check.getLocal().getLastModified();
                    long remoteModified = check.getRemote().getLastModified();
                    check.setSyncType(
                            localModified > remoteModified ? SyncType.UPLOAD :
                                    localModified < remoteModified ? SyncType.DOWNLOAD :
                                    SyncType.NO_NEED
                    );
                    return check;
                })
                .toObservable();
    }

    private Observable<Progressible<CheckPair>> sync(CheckPair checkPair){
        switch (checkPair.getSyncType()){
            case UPLOAD:
                return upload(checkPair.getLocal())
                        .map(progress -> progress.map(checkPair));
            case DOWNLOAD:
                return download(checkPair.getRemote())
                        .map(progress -> progress.map(checkPair));

            default:
                return Observable.just(new Progressible<>(checkPair, 0, 0, checkPair.getLocal().getName(), true));
        }
    }

    private Observable<Progressible<AuroraFile>> upload(AuroraFile local){
        return mInteractor.upload(local);
    }

    private Observable<Progressible<AuroraFile>> download(AuroraFile remote){
        return mInteractor.download(remote);
    }

    private <T> Progressible<T> mapProgressToPercent(Progressible<T> progress){
        float p = progress.getMax() > 0 ?
                ((float)progress.getProgress() / progress.getMax()) * 100 : -1;
        progress.setMax(100);
        progress.setProgress((long) p);
        return progress;
    }
}
