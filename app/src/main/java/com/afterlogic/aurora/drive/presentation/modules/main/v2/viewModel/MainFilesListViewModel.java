package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableBoolean;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.application.navigation.args.OpenExternalArgs;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.FileListArgs;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewArgs;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor.MainFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFileActionCallback;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFileActionRequest;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListViewModel extends SearchableFileListViewModel<MainFilesListViewModel, MainFileViewModel, FileListArgs> {

    public final ObservableBoolean isMultiChoise = new ObservableBoolean();

    private final MainFilesListInteractor interactor;
    private final Subscriber subscriber;
    private final FilesMapper mapper;
    private final Router router;
    private final AppResources appResources;

    private OptionalDisposable thumbsDisposable = new OptionalDisposable();
    private OptionalDisposable offlineStatusDisposable = new OptionalDisposable();
    private OptionalDisposable syncProgressDisposable = new OptionalDisposable();

    private boolean fileActionMode = false;

    @Nullable
    private List<AuroraFile> files;

    @Inject
    MainFilesListViewModel(MainFilesListInteractor interactor,
                           Subscriber subscriber,
                           ViewModelsConnection<MainFilesListViewModel> viewModelsConnection,
                           FilesMapper mapper,
                           Router router,
                           AppResources appResources) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.mapper = mapper;
        this.router = router;
        this.appResources = appResources;

        mapper.setOnLongClickListner((position, item) -> onFileLongClick(item));
    }

    @Override
    protected MainFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return mapper.map(file, onItemClickListener);
    }

    @Override
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

        this.files = files;

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

    @Override
    protected void reloadCurrentFolder() {
        files = null;
        super.reloadCurrentFolder();
    }

    @Override
    protected void onFileClick(AuroraFile file) {
        super.onFileClick(file);

        if (isMultiChoise.get()){
            MainFileViewModel vm = mapper.get(file);
            if (vm == null) return;

            vm.selected.set(!vm.selected.get());
            return;
        }

        if (file.isFolder()){ // TODO: isListAction
            super.onFileClick(file);
        } else {

            // TODO: check can open
            //if (!mRouter.canOpenFile(file)){
            //    onCantOpenFile();
            //    return;
            //}

            if (file.isLink()){
                router.navigateTo(AppRouter.EXTERNAL_BROWSER, file.getLinkUrl());
            }else {
                if (file.isPreviewAble()){
                    FileViewArgs args = new FileViewArgs(file, files);
                    router.navigateTo(AppRouter.IMAGE_VIEW, args);
                }else {
                    interactor.downloadForOpen(file)
                            .doOnNext(progress -> {
                                float value = progress.getMax() > 0 && progress.getProgress() >= 0 ?
                                        (float) progress.getProgress() / progress.getMax() : -1;
                                this.progress.set(new ProgressViewModel(
                                        appResources.getString(R.string.dialog_downloading),
                                        progress.getName(),
                                        value == -1,
                                        (int) (100 * value),
                                        100

                                ));
                            })
                            .doFinally(() -> this.progress.set(null))
                            .filter(Progressible::isDone)
                            .map(Progressible::getData)
                            .compose(subscriber::defaultSchedulers)
                            .subscribe(subscriber.subscribe(localFile -> {
                                OpenExternalArgs args = new OpenExternalArgs(file, localFile);
                                router.navigateTo(AppRouter.EXTERNAL_OPEN_FILE, args);
                            }));
                }
            }
        }
    }

    private void onFileLongClick(AuroraFile file) {
        MainFileViewModel fileVM = mapper.get(file);
        if (fileVM == null) return;

        interactor.setFileForAction(new MainFileActionRequest(file, fileVM.icon, fileVM.isOffline, new MainFileActionCallback() {
            @Override
            public void onRenameFileAction() {
                onFileAction();
            }

            @Override
            public void onDeleteFileAction() {
                onFileAction();
            }

            @Override
            public void onDownloadFileAction() {
                onFileAction();
            }

            @Override
            public void onShareFileAction() {
                onFileAction();
            }

            @Override
            public void onMakeOfflineFileAction(boolean offline) {
                onFileAction();
            }

            @Override
            public void onMakePublicLink(boolean publicLink) {
                onFileAction();
            }

            @Override
            public void onCopyPublicLinkFileAction() {
                onFileAction();
            }

            @Override
            public void onCopyFileAction() {
                onFileAction();
            }

            @Override
            public void onReplaceFileAction() {
                onFileAction();
            }
        }));

        router.navigateTo(AppRouter.MAIN_FILE_ACTIONS);
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

    private void onFileAction() {
        interactor.setFileForAction(null);
        fileActionMode = false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (fileActionMode) {
            interactor.setFileForAction(null);
        }
    }
}
