package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableField;
import android.support.v4.util.Pair;
import android.view.View;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.application.navigation.args.ExternalOpenFileArgs;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.MessageDialogViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.commands.ContextMenuCommand;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.offline.interactor.OfflineFileListInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFileListViewModel extends SearchableFileListViewModel<OfflineFileListViewModel, OfflineFileViewModel, OfflineArgs> {

    public final ObservableField<OfflineHeader> header = new ObservableField<>();
    public final ObservableField<MessageDialogViewModel> message = new ObservableField<>();
    public final ContextMenuCommand fileContextCommand = new ContextMenuCommand();

    private final OfflineFileListInteractor interactor;
    private final Subscriber subscriber;
    private final FileMapper mapper;
    private final AppRouter router;
    private final AppResources appResources;

    private final OptionalDisposable thumbnailsDisposable = new OptionalDisposable();
    private final OptionalDisposable syncListenerDisposable = new OptionalDisposable();
    private final OptionalDisposable offlineStatusDisposable = new OptionalDisposable();
    private final DisposableBag globalDisposableBag = new DisposableBag();

    @Inject
    OfflineFileListViewModel(OfflineFileListInteractor interactor,
                             Subscriber subscriber,
                             ViewModelsConnection<OfflineFileListViewModel> viewModelsConnection,
                             FileMapper mapper,
                             AppRouter router,
                             AppResources appResources) {
        super(interactor, subscriber, viewModelsConnection);
        this.mapper = mapper;
        this.subscriber = subscriber;
        this.interactor = interactor;
        this.router = router;
        this.appResources = appResources;

        mapper.setOnLongClickListener(this::onFileLongClick);
    }

    @Override
    public void setArgs(OfflineArgs args) {
        super.setArgs(args);
        header.set(new OfflineHeader(args.isManual()));
    }

    @Override
    protected void handleFiles(List<AuroraFile> files) {
        mapper.clear();
        super.handleFiles(files);
        updateOfflineStatuses(files);
        updateThumbnails(files);
    }

    @Override
    protected OfflineFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return mapper.map(file, onItemClickListener);
    }

    @Override
    protected void onFileClick(AuroraFile file) {

        OfflineFileViewModel vm = mapper.get(file);
        if (vm == null) {
            return;
        }

        if (vm.syncProgress.get() == -1) {

            interactor.downloadForOpen(file)
                    .compose(subscriber::defaultSchedulers)
                    .filter(Progressible::isDone)
                    .map(Progressible::getData)
                    .subscribe(subscriber.subscribe(localFile -> {
                        ExternalOpenFileArgs args = new ExternalOpenFileArgs(file, localFile);
                        router.navigateTo(
                                AppRouter.EXTERNAL_OPEN_FILE, args,
                                error -> MessageDialogViewModel.set(
                                        message, null,
                                        appResources.getString(R.string.prompt_cant_open_file)
                                )
                        );
                    }));

        } else {

            MessageDialogViewModel.set(
                    message, null,
                    appResources.getString(R.string.prompt_offline_file_in_sync)
            );

        }
    }

    private void onFileLongClick(View view, AuroraFile file) {
        fileContextCommand.fireFor(view)
                .addAction(
                        appResources.getString(R.string.prompt_offline_disable_offline),
                        () -> onDeleteFromOffline(file)
                )
                .buildAndSet();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        globalDisposableBag.dispose();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected void startListenSync() {
        interactor.listenSyncProgress()
                .compose(subscriber::defaultSchedulers)
                .compose(syncListenerDisposable::disposeAndTrack)
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(this::handleSyncProgress));

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void stopListenSync() {
        syncListenerDisposable.disposeAndClear();
    }

    private void onDeleteFromOffline(AuroraFile file) {
        interactor.disableOffline(file)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(() -> {
                    OfflineFileViewModel vm = mapper.get(file);
                    if (vm != null) {
                        items.remove(vm);
                    }
                }));
    }

    private void handleSyncProgress(SyncProgress progress) {
        OfflineFileViewModel vm = mapper.get(progress.getFilePathSpec());

        MyLog.d("Status: " + progress);

        if (vm != null) {
            vm.syncProgress.set(progress.isDone() ? -1 : Math.max(0, progress.getProgress()));
        }
    }

    private void updateOfflineStatuses(List<AuroraFile> files) {
        Stream.of(files)
                .map(file -> interactor.checkIsSynced(file)
                        .filter(status -> !status) // handle only not synced
                        .map(status -> new Pair<>(file, status))
                        .toObservable()
                )
                .collect(Observables.Collectors.concatObservables())
                .compose(subscriber::defaultSchedulers)
                .compose(offlineStatusDisposable::disposeAndTrack)
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(statusPair -> {
                    OfflineFileViewModel vm = mapper.get(statusPair.first);
                    if (vm != null) {
                        vm.syncProgress.set(-2);
                    }
                }));
    }

    private void updateThumbnails(List<AuroraFile> files) {
        Stream.of(files)
                .map(file -> interactor.getThumbnail(file)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess(thumbnail -> {
                            OfflineFileViewModel vm = mapper.get(file);
                            if (vm != null) {
                                vm.setThumbnail(thumbnail);
                            }
                        })
                        .toCompletable()
                        .onErrorComplete()
                )
                .collect(Observables.Collectors.concatCompletable())
                .compose(subscriber::defaultSchedulers)
                .compose(thumbnailsDisposable::disposeAndTrack)
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.justSubscribe());
    }
}
