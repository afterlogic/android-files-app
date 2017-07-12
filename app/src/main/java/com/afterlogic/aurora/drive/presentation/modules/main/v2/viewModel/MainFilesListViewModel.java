package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;

import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.BaseFileListArgs;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor.MainFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListViewModel extends SearchableFileListViewModel<MainFilesListViewModel, MainFileViewModel, BaseFileListArgs> {

    private final MainFilesListInteractor interactor;
    private final Subscriber subscriber;
    private final FilesMapper mapper;

    private OptionalDisposable thumbsDisposable = new OptionalDisposable();
    private OptionalDisposable offlineStatusDisposable = new OptionalDisposable();
    private OptionalDisposable syncProgressDisposable = new OptionalDisposable();

    @Inject
    MainFilesListViewModel(MainFilesListInteractor interactor,
                           Subscriber subscriber,
                           ViewModelsConnection<MainFilesListViewModel> viewModelsConnection,
                           FilesMapper mapper) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.mapper = mapper;

    }

    @Override
    protected MainFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return mapper.map(file, onItemClickListener);
    }

    @Override
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        super.onStart();

        Stream.of(items).forEach(vm -> vm.syncProgress.set(-1));

        interactor.getSyncProgress()
                .compose(syncProgressDisposable::disposeAndTrack)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(this::handleSyncProgress));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        syncProgressDisposable.disposeAndClear();
    }

    @Override
    protected void handleFiles(List<AuroraFile> files) {
        mapper.clear();

        super.handleFiles(files);

        Stream.of(files)
                .filter(AuroraFile::hasThumbnail)
                .map(this::updateFileThumb)
                .collect(Observables.Collectors.concatCompletable())
                .compose(thumbsDisposable::disposeAndTrack)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.justSubscribe());

        Stream.of(files)
                .map(this::checkOfflineStatus)
                .collect(Observables.Collectors.concatCompletable())
                .compose(offlineStatusDisposable::disposeAndTrack)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.justSubscribe());
    }


    private Completable updateFileThumb(AuroraFile file){
        if (!file.hasThumbnail()) return Completable.complete();

        return interactor.getThumbnail(file)
                .doOnSuccess(thumb -> {
                    MainFileViewModel vm = mapper.get(file);
                    if (vm != null) vm.setThumbnail(thumb);
                })
                .toCompletable()
                .onErrorComplete();
    }

    private Completable checkOfflineStatus(AuroraFile file) {
        return interactor.getOfflineStatus(file)
                .doOnSuccess(offline -> {
                    MainFileViewModel vm = mapper.get(file);
                    if (vm != null) vm.isOffline.set(offline);
                })
                .toCompletable()
                .onErrorComplete();
    }

    private void handleSyncProgress(SyncProgress progress) {
        MainFileViewModel vm = mapper.get(progress.getFilePathSpec());
        if (vm != null) vm.syncProgress.set(progress.isDone() ? -1 : progress.getProgress());
    }
}
