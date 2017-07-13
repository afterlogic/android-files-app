package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.application.navigation.args.OpenExternalArgs;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Observables;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnListChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.FileListArgs;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFileListViewModel;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewArgs;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor.MainFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFileAction;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFileActionsFile;
import com.afterlogic.aurora.drive.presentation.modules.mainFIlesAction.interactor.MainFilesActionsInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.subjects.PublishSubject;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFilesListViewModel extends SearchableFileListViewModel<MainFilesListViewModel, MainFileViewModel, FileListArgs> {

    public final ObservableBoolean multiChoiceMode = new ObservableBoolean();

    private final ObservableList<AuroraFile> selectedFiles = new ObservableArrayList<>();

    private final MainFilesListInteractor interactor;
    private final MainFilesActionsInteractor filesActionsInteractor;
    private final Subscriber subscriber;
    private final FilesMapper mapper;
    private final Router router;
    private final AppResources appResources;

    private OptionalDisposable thumbsDisposable = new OptionalDisposable();
    private OptionalDisposable offlineStatusDisposable = new OptionalDisposable();
    private OptionalDisposable syncProgressDisposable = new OptionalDisposable();

    private PublishSubject<String> setFileTypePublisher = PublishSubject.create();

    private DisposableBag globalDisposableBag = new DisposableBag();

    private boolean fileActionMode = false;

    @Nullable
    private List<AuroraFile> files;

    @Inject
    MainFilesListViewModel(MainFilesListInteractor interactor,
                           MainFilesActionsInteractor filesActionsInteractor, Subscriber subscriber,
                           MainViewModelsConnection viewModelsConnection,
                           FilesMapper mapper,
                           Router router,
                           AppResources appResources) {
        super(interactor, subscriber, viewModelsConnection);
        this.interactor = interactor;
        this.filesActionsInteractor = filesActionsInteractor;
        this.subscriber = subscriber;
        this.mapper = mapper;
        this.router = router;
        this.appResources = appResources;

        mapper.setOnLongClickListner((position, item) -> onFileLongClick(item));

        viewModelsConnection.getMultiChoiceMode()
                .compose(globalDisposableBag::track)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(multiChoiceMode::set));

        setFileTypePublisher.firstElement()
                .flatMapObservable(viewModelsConnection::listenMultiChoiceAction)
                .compose(globalDisposableBag::track)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(this::handleMultiChoiceAction));

        setFileTypePublisher.firstElement()
                .flatMapObservable(viewModelsConnection::listenMainAction)
                .compose(globalDisposableBag::track)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(this::handleMainAction));

        SimpleOnPropertyChangedCallback.addTo(multiChoiceMode, mode -> onMultiChoiceModeChanged(mode.get()));

        SimpleOnListChangedCallback.addTo(
                selectedFiles,
                list -> viewModelsConnection.setMultiChoice(new ArrayList<>(selectedFiles))
        );
    }

    @Override
    public void setArgs(FileListArgs args) {
        super.setArgs(args);
        setFileTypePublisher.onNext(args.getType());
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
        selectedFiles.clear();
        super.reloadCurrentFolder();
    }

    @Override
    protected void onFileClick(AuroraFile file) {
        if (multiChoiceMode.get()){
            onFileClickedInMultiChoiceMode(file);
            return;
        }

        if (file.isFolder()){ // TODO: isListAction
            super.onFileClick(file);
        } else {

            if (file.isLink()){
                router.navigateTo(AppRouter.EXTERNAL_BROWSER, file.getLinkUrl());
            }else {
                if (file.isPreviewAble()){
                    FileViewArgs args = new FileViewArgs(file, files);
                    router.navigateTo(AppRouter.IMAGE_VIEW, args);
                }else {
                    interactor.downloadForOpen(file)
                            .compose(subscriber::defaultSchedulers)
                            .doOnNext(progress -> {

                                float value = progress.getMax() > 0 && progress.getProgress() >= 0 ?
                                        (float) progress.getProgress() / progress.getMax() : -1;

                                if (value == -1) {
                                    this.progress.set(ProgressViewModel.Factory.indeterminateProgress(
                                            appResources.getString(R.string.dialog_downloading),
                                            progress.getName()
                                    ));
                                } else {
                                    this.progress.set(ProgressViewModel.Factory.progress(
                                            appResources.getString(R.string.dialog_downloading),
                                            progress.getName(),
                                            (int) (100 * value),
                                            100
                                    ));
                                }
                            })

                            .doFinally(() -> this.progress.set(null))
                            .filter(Progressible::isDone)
                            .map(Progressible::getData)
                            .subscribe(subscriber.subscribe(localFile -> {
                                OpenExternalArgs args = new OpenExternalArgs(file, localFile);
                                router.navigateTo(AppRouter.EXTERNAL_OPEN_FILE, args);
                            }));
                }
            }
        }
    }

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

    private void handleMultiChoiceAction(MultiChoiceAction action) {
        MyLog.d(getFileType() + ":handleMultiChoiceAction:" + action);

    }

    private void handleMainAction(MainAction action) {
        MyLog.d(getFileType() + ":handleMainAction:" + action);
    }

    private void onFileLongClick(AuroraFile file) {
        MainFileViewModel fileVM = mapper.get(file);
        if (fileVM == null) return;

        filesActionsInteractor.setFileForAction(new MainFileActionsFile(file, fileVM.icon, fileVM.isOffline))
                .firstElement()
                .compose(subscriber::defaultSchedulers)
                .doOnSubscribe(disposable -> {
                    fileActionMode = true;
                    router.navigateTo(AppRouter.MAIN_FILE_ACTIONS);
                })
                .doFinally(() -> fileActionMode = false)
                .compose(globalDisposableBag::track)
                .subscribe(subscriber.subscribe(this::handleFileAction));
    }

    private void handleFileAction(MainFileAction action) {
        MyLog.d(getFileType() + ":handleFileAction:" + action);
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

    private void onMultiChoiceModeChanged(boolean multiChoice) {
        if (multiChoice) {
            selectedFiles.clear();
        } else {
            Stream.of(items)
                    .filter(item -> item.selected.get())
                    .forEach(item -> item.selected.set(false));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        globalDisposableBag.dispose();
    }
}
