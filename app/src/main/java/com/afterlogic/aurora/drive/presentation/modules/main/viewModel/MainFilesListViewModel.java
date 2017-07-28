package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.ActivityNotFoundException;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.application.navigation.args.ExternalOpenFIleArgs;
import com.afterlogic.aurora.drive.application.navigation.args.ExternalShareFileArgs;
import com.afterlogic.aurora.drive.application.navigation.args.ExternalShareFilesArgs;
import com.afterlogic.aurora.drive.application.navigation.args.ReplaceScreenArgs;
import com.afterlogic.aurora.drive.core.common.contextWrappers.Toaster;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnListChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.MessageDialogViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.FileListArgs;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.rx.FileProgressTransformer;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.rx.IndeterminateProgressTransformer;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.rx.TrackInMapTransformer;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewArgs;
import com.afterlogic.aurora.drive.presentation.modules.main.interactor.MainFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor.MainFileAction;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor.MainFileActionsFile;
import com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor.MainFilesActionsInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.annimon.stream.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListViewModel extends SearchableFileListViewModel<MainFilesListViewModel, MainFileViewModel, FileListArgs> {

    public final ObservableField<MessageDialogViewModel> messageDialog = new ObservableField<>();

    private final ObservableBoolean multiChoiceMode = new ObservableBoolean();
    private final ObservableList<AuroraFile> selectedFiles = new ObservableArrayList<>();

    private final MainFilesListInteractor interactor;
    private final MainFilesActionsInteractor filesActionsInteractor;
    private final Subscriber subscriber;
    private final FilesMapper mapper;
    private final AppRouter router;
    private final AppResources appResources;
    private final Toaster toaster;
    private final MainViewModelsConnection viewModelsConnection;

    private boolean reloadAtStart = false;

    private OptionalDisposable thumbsDisposable = new OptionalDisposable();
    private OptionalDisposable offlineStatusDisposable = new OptionalDisposable();
    private OptionalDisposable syncProgressDisposable = new OptionalDisposable();

    private PublishSubject<String> setFileTypePublisher = PublishSubject.create();

    private Map<AuroraFile, Disposable> publicLinkDisposables = new HashMap<>();
    private DisposableBag globalDisposableBag = new DisposableBag();

    @Nullable
    private List<AuroraFile> files = null;

    @Nullable
    private AuroraFile fileForAction = null;

    @Inject
    MainFilesListViewModel(MainFilesListInteractor interactor,
                           MainFilesActionsInteractor filesActionsInteractor,
                           Subscriber subscriber,
                           MainViewModelsConnection viewModelsConnection,
                           FilesMapper mapper,
                           AppRouter router,
                           AppResources appResources,
                           Toaster toaster) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;
        this.filesActionsInteractor = filesActionsInteractor;
        this.subscriber = subscriber;
        this.mapper = mapper;
        this.router = router;
        this.appResources = appResources;
        this.toaster = toaster;
        this.viewModelsConnection = viewModelsConnection;

        mapper.setOnLongClickListener((position, item) -> onFileLongClick(item));

        viewModelsConnection.getMultiChoiceMode()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(multiChoiceMode::set));

        setFileTypePublisher.firstElement()
                .flatMapObservable(viewModelsConnection::listenMultiChoiceAction)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(this::handleMultiChoiceAction));

        setFileTypePublisher.firstElement()
                .flatMapObservable(viewModelsConnection::listenMainAction)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(this::handleMainAction));

        SimpleOnPropertyChangedCallback.addTo(multiChoiceMode, mode -> onMultiChoiceModeChanged(mode.get()));

        SimpleOnListChangedCallback.addTo(selectedFiles, this::onSelectedFilesChanged);

        SimpleOnPropertyChangedCallback.addTo(viewModelState, this::onViewModelStateChanged);
    }

    // region Base
    @Override
    public void setArgs(FileListArgs args) {
        super.setArgs(args);
        setFileTypePublisher.onNext(args.getType());
    }

    @Override
    protected MainFileViewModel mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener) {
        return mapper.mapAndStore(file, onItemClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (reloadAtStart) {
            reloadAtStart = false;
            reloadCurrentFolder();
        }

        Stream.of(items).forEach(vm -> vm.syncProgress.set(-1));

        interactor.getSyncProgress()
                .compose(subscriber::defaultSchedulers)
                .compose(syncProgressDisposable::disposeAndTrack)
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
                .compose(subscriber::defaultSchedulers)
                .compose(thumbsDisposable::disposeAndTrack)
                .subscribe(subscriber.justSubscribe());

        Stream.of(files)
                .map(this::checkOfflineStatus)
                .collect(Observables.Collectors.concatCompletable())
                .compose(subscriber::defaultSchedulers)
                .compose(offlineStatusDisposable::disposeAndTrack)
                .subscribe(subscriber.justSubscribe());
    }

    @Override
    protected void reloadCurrentFolder() {
        files = null;
        selectedFiles.clear();
        super.reloadCurrentFolder();
    }

    @Override
    protected void onFileClick(AuroraFile file) {
        if (multiChoiceMode.get()){
            onFileClickedInMultiChoiceMode(file);
            return;
        }

        if (file.isFolder()) {
            super.onFileClick(file);
        } else {

            if (file.getActions() != null && file.getActions().hasList()) {
                foldersStack.add(0, file);
                return;
            }

            if (file.isLink()){
                router.navigateTo(AppRouter.EXTERNAL_BROWSER, file.getLinkUrl(), this::onOpenFileError);
            }else {
                if (file.isPreviewAble()){
                    FileViewArgs args = new FileViewArgs(file, files);
                    router.navigateTo(AppRouter.IMAGE_VIEW, args);
                }else {
                    interactor.downloadForOpen(file)
                            .compose(subscriber::defaultSchedulers)
                            .compose(cancellableLoadProgress(appResources.getString(R.string.dialog_downloading)))
                            .filter(Progressible::isDone)
                            .map(Progressible::getData)
                            .subscribe(subscriber.subscribe(localFile -> {
                                ExternalOpenFIleArgs args = new ExternalOpenFIleArgs(file, localFile);
                                router.navigateTo(AppRouter.EXTERNAL_OPEN_FILE, args, this::onOpenFileError);
                            }));
                }
            }
        }
    }

    private void onOpenFileError(Throwable e) {
        if (e instanceof ActivityNotFoundException) {
            MessageDialogViewModel.set(
                    messageDialog, null, appResources.getString(R.string.prompt_cant_open_file)
            );
        } else {
            MyLog.majorException(e);
        }
    }

    private void onViewModelStateChanged() {
        if (viewModelState.get() == ViewModelState.ERROR) {
            interactor.getNetworkState()
                    .subscribe(subscriber.subscribe(networkEnabled -> {
                        if (!networkEnabled) {
                            router.newRootScreen(AppRouter.OFFLINE, false);
                        }
                    }));
        }
    }

    // endregion

    // region MultiChoice actions

    private void onFileClickedInMultiChoiceMode(AuroraFile file) {
        MainFileViewModel vm = mapper.get(file);
        if (vm == null) return;

        boolean selected = !vm.selected.get();
        vm.selected.set(selected);
        if (selected) {
            if (!selectedFiles.contains(file)) {
                selectedFiles.add(file);
            }
        } else {
            if (selectedFiles.contains(file)) {
                selectedFiles.remove(file);
            }
        }
    }

    private void onSelectedFilesChanged(List<AuroraFile> selectedFiles) {
        List<MultiChoiceFile> multiChoiceFiles = Stream.of(selectedFiles)
                .map(file -> {
                    // TODO: Maybe get from interactor?
                    MainFileViewModel vm = mapper.get(file);
                    return new MultiChoiceFile(file, vm != null && vm.isOffline.get());
                })
                .toList();

        viewModelsConnection.setMultiChoice(multiChoiceFiles);
    }

    private void handleMultiChoiceAction(MultiChoiceAction action) {
        MyLog.d(getFileType() + ":handleMultiChoiceAction:" + action);

        List<AuroraFile> filesForAction = new ArrayList<>(selectedFiles);

        if (filesForAction.size() == 0) {
            MyLog.d("Ignore multichoice actions cause selected files is empty.");
            return;
        }

        switch (action) {
            case DELETE: multiChoiceDelete(filesForAction); break;
            case DOWNLOAD: multiChoiceDownload(filesForAction); break;
            case SHARE: multiChoiceShare(filesForAction); break;
            case REPLACE: multiChoiceReplace(filesForAction); break;
            case COPY: multiChoiceCopy(filesForAction); break;
            case TOGGLE_OFFLINE: multiChoiceToggleOffline(filesForAction); break;
        }

        selectedFiles.clear();
    }

    private void multiChoiceDelete(List<AuroraFile> files) {
        Stream.of(files)
                .map(file -> interactor.deleteFile(file)
                        .compose(subscriber::defaultSchedulers)
                        .doOnComplete(() -> {
                            MainFileViewModel vm = mapper.get(file);
                            if (vm != null) {
                                items.remove(vm);
                            }
                        })
                )
                .collect(Observables.Collectors.concatCompletable())
                .compose(new IndeterminateProgressTransformer(this,
                        appResources.getString(R.string.prompt_dialog_title_deleting),
                        appResources.getPlurals(R.plurals.files, files.size(), files.size())
                ))
                .subscribe(subscriber.justSubscribe());
    }

    private void multiChoiceDownload(List<AuroraFile> files) {
        Stream.of(files)
                .map(interactor::downloadToDownloads)
                .collect(Observables.Collectors.concatObservables())
                .compose(subscriber::defaultSchedulers)
                .compose(cancellableLoadProgress(appResources.getString(R.string.dialog_files_title_dowloading)))
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .collectInto(new ArrayList<File>(), List::add)
                .subscribe(subscriber.subscribe(results -> MessageDialogViewModel.set(
                        messageDialog,
                        null,
                        appResources.getPlurals(
                                R.plurals.dialog_files_success_downloaded,
                                results.size(), results.size()
                        )
                )));
    }

    private void multiChoiceShare(List<AuroraFile> files) {
        Stream.of(files)
                .map(interactor::downloadForOpen)
                .collect(Observables.Collectors.concatObservables())
                .compose(subscriber::defaultSchedulers)
                .compose(cancellableLoadProgress(appResources.getString(R.string.dialog_files_title_dowloading)))
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .collectInto(new ArrayList<File>(), List::add)
                .subscribe(subscriber.subscribe(results -> {
                    ExternalShareFilesArgs args = new ExternalShareFilesArgs(results);
                    router.navigateTo(AppRouter.EXTERNAL_SHARE, args, error -> {
                        MessageDialogViewModel.set(
                                messageDialog, null,
                                appResources.getString(R.string.prompt_cant_open_file
                                ));
                    });
                }));
    }

    private void multiChoiceToggleOffline(List<AuroraFile> files) {
        Stream.of(files)
                .map(file -> interactor.getOfflineStatus(file).toObservable())
                .collect(Observables.Collectors.concatObservables())
                .any(offline -> !offline)
                .flatMapCompletable(offline ->
                        Stream.of(files)
                                .map(file -> interactor.getOfflineStatus(file)
                                        .filter(currentOffline -> currentOffline != offline)
                                        .flatMapCompletable(itemOffline -> interactor.setOffline(file, offline))
                                        .doOnComplete(() -> {
                                            MainFileViewModel vm = mapper.get(file);
                                            if (vm != null) {
                                                vm.isOffline.set(offline);
                                            }
                                        })
                                )
                                .collect(Observables.Collectors.concatCompletable())
                )
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.justSubscribe());
    }

    private void multiChoiceReplace(List<AuroraFile> files) {
        reloadAtStart = true;
        router.navigateTo(AppRouter.REPLACE, new ReplaceScreenArgs(files));
    }

    private void multiChoiceCopy(List<AuroraFile> files) {
        reloadAtStart = true;
        router.navigateTo(AppRouter.COPY, new ReplaceScreenArgs(files));
    }

    // endregion

    // region Main activity actions

    private void handleMainAction(MainAction action) {
        MyLog.d(getFileType() + ":handleMainAction:" + action);

        switch (action) {
            case CREATE_FOLDER: createFolder(); break;
            case UPLOAD_FILE: uploadFile(); break;
        }
    }

    private void createFolder() {
        interactor.getNewFolderName()
                .observeOn(Schedulers.io())
                .flatMap(this::createFolder)
                .compose(globalDisposableBag::track)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber.subscribe(folder -> foldersStack.add(0, folder)));
    }

    private Maybe<AuroraFile> createFolder(String name) {
        return interactor.createNewFolder(name, foldersStack.get(0))
                .compose(new IndeterminateProgressTransformer<>(this,
                        appResources.getString(R.string.prompt_dialog_title_folder_creation),
                        name
                ))
                .toMaybe();
    }

    private void uploadFile() {
        interactor.getFileForUpload()
                .observeOn(Schedulers.io())
                .flatMap(this::uploadFile)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(this::reloadCurrentFolder));
    }

    private Single<AuroraFile> uploadFile(Uri file) {
        return interactor.uploadFile(foldersStack.get(0), file)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(cancellableLoadProgress(appResources.getString(R.string.dialog_files_title_uploading)))
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .firstOrError();
    }

    // endregion

    // region File actions

    private void onFileLongClick(AuroraFile file) {
        MainFileViewModel fileVM = mapper.get(file);
        if (fileVM == null) return;

        filesActionsInteractor.setFileForAction(new MainFileActionsFile(file, fileVM.icon, fileVM.isOffline))
                .firstElement()
                .compose(subscriber::defaultSchedulers)
                .doOnSubscribe(disposable -> {
                    fileForAction = file;
                    router.navigateTo(AppRouter.MAIN_FILE_ACTIONS);
                })
                .doFinally(() -> fileForAction = null)
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(this::handleFileAction));
    }

    private void handleFileAction(MainFileAction action) {
        MyLog.d(getFileType() + ":handleFileAction:" + action);

        if (fileForAction == null) {
            MyLog.e("Ignore action for file cause file is null.");
            return;
        }

        switch (action) {
            case RENAME: renameFile(fileForAction); break;
            case DELETE: deleteFile(fileForAction); break;
            case DOWNLOAD: downloadFile(fileForAction); break;
            case SHARE: shareFile(fileForAction); break;
            case OFFLINE: toggleOffline(fileForAction); break;
            case PUBLIC_LINK: togglePublicLink(fileForAction); break;
            case COPY_PUBLIC_LINK: copyPublicLink(fileForAction); break;
            case REPLACE: replaceTo(fileForAction); break;
            case COPY: copyTo(fileForAction); break;
        }
    }

    private void renameFile(AuroraFile file) {
        interactor.getNewFileName(file)
                .observeOn(Schedulers.io())
                .flatMap(name -> renameFile(file, name).toMaybe())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(newFile -> handleFileRenaming(file, newFile)));
    }

    private void handleFileRenaming(AuroraFile file, AuroraFile newFile) {
        MainFileViewModel vm = mapper.remove(file);
        if (vm != null) {
            items.remove(vm);

            MainFileViewModel newVM = mapper.mapAndStore(newFile, (p, item) -> onFileClick(item));

            int newPosition = Stream.of(mapper.getKeys())
                    .sorted(FileUtil.AURORA_FILE_COMPARATOR)
                    .toList()
                    .indexOf(newFile);

            if (newPosition != -1) {
                items.add(newPosition, newVM);

                updateFileThumb(newFile)
                        .compose(subscriber::defaultSchedulers)
                        .subscribe();
            }
        }
    }

    private Single<AuroraFile> renameFile(AuroraFile file, String newName) {
        return interactor.rename(file, newName)
                .compose(new IndeterminateProgressTransformer<>(this,
                        appResources.getString(R.string.prompt_dialog_title_renaming),
                        file.getName()
                ));
    }

    private void deleteFile(AuroraFile file) {
        interactor.deleteFile(file)
                .compose(subscriber::defaultSchedulers)
                .compose(new IndeterminateProgressTransformer(this,
                        appResources.getString(R.string.prompt_dialog_title_deleting),
                        file.getName()
                ))
                .subscribe(subscriber.subscribe(() -> {
                    MainFileViewModel vm = mapper.get(file);
                    if (vm != null) {
                        items.remove(vm);
                    }
                }));
    }

    private void downloadFile(AuroraFile file) {
        interactor.downloadToDownloads(file)
                .compose(subscriber::defaultSchedulers)
                .compose(cancellableLoadProgress(appResources.getString(R.string.dialog_files_title_dowloading)))
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .subscribe(subscriber.subscribe(local -> router.navigateTo(
                        AppRouter.EXTERNAL_OPEN_FILE, new ExternalOpenFIleArgs(file, local), error -> {
                            MessageDialogViewModel.set(
                                    messageDialog, null,
                                    appResources.getString(R.string.prompt_cant_open_file
                                    ));
                        }
                )));
    }

    private void shareFile(AuroraFile file) {
        interactor.downloadForOpen(file)
                .compose(subscriber::defaultSchedulers)
                .compose(cancellableLoadProgress(appResources.getString(R.string.dialog_files_title_dowloading)))
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .subscribe(subscriber.subscribe(local ->router.navigateTo(
                        AppRouter.EXTERNAL_SHARE, new ExternalShareFileArgs(file, local), error -> {
                            MessageDialogViewModel.set(
                                    messageDialog, null,
                                    appResources.getString(R.string.prompt_cant_open_file
                                    ));
                        }
                )));
    }

    private void toggleOffline(AuroraFile file) {
        interactor.getOfflineStatus(file)
                .map(offline -> !offline)
                .flatMap(offline -> interactor.setOffline(file, offline)
                        .andThen(Single.just(offline))
                )
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(offline -> {
                    MainFileViewModel vm = mapper.get(file);
                    if (vm != null) {
                        vm.isOffline.set(offline);
                    }
                }));
    }

    private void togglePublicLink(AuroraFile file) {
        if (file.isShared()) {
            interactor.deletePublicLink(file)
                    .compose(subscriber::defaultSchedulers)
                    .compose(new TrackInMapTransformer<>(file, publicLinkDisposables))
                    .subscribe(() -> {
                        file.setShared(false);
                        MainFileViewModel vm = mapper.get(file);
                        if (vm != null) {
                            vm.shared.set(false);
                        }
                    });
        } else {
            createPublicLink(file, R.string.prompt_public_link_created);
        }
    }

    private void copyPublicLink(AuroraFile file) {
        createPublicLink(file, R.string.prompt_public_link_copied);
    }

    private void replaceTo(AuroraFile file) {
        reloadAtStart = true;
        router.navigateTo(AppRouter.REPLACE, new ReplaceScreenArgs(file));
    }

    private void copyTo(AuroraFile file) {
        reloadAtStart = true;
        router.navigateTo(AppRouter.COPY, new ReplaceScreenArgs(file));
    }

    private void createPublicLink(AuroraFile file, int messageTestId) {
        interactor.createPublicLink(file)
                .compose(subscriber::defaultSchedulers)
                .compose(new TrackInMapTransformer<>(file, publicLinkDisposables))
                .subscribe(() -> {
                    file.setShared(true);
                    MainFileViewModel vm = mapper.get(file);
                    if (vm != null) {
                        vm.shared.set(true);
                    }

                    toaster.showShort(messageTestId);
                });
    }

    // endregion

    // region Other
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

    private void onMultiChoiceModeChanged(boolean multiChoice) {
        if (multiChoice) {
            selectedFiles.clear();
        } else {
            Stream.of(items)
                    .filter(item -> item.selected.get())
                    .forEach(item -> item.selected.set(false));
        }
    }

    private <T extends Progressible> ObservableTransformer<T, T> cancellableLoadProgress(String title) {
        return new FileProgressTransformer<>(title, progress);
    }

    // endregion

    @Override
    protected void onCleared() {
        super.onCleared();
        globalDisposableBag.dispose();
    }

}
