package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.core.common.streams.StreamCollectors;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnListChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceFileTypeInteractor;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceFileTypeArgs;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeViewModel extends BaseViewModel {

    public final ObservableField<ViewModelState> viewModelState = new UiObservableField<>();
    public final ObservableList<ReplaceFileViewModel> items = new ObservableArrayList<>();
    public final ObservableField<ProgressViewModel> progress = new UiObservableField<>(null);

    private final ReplaceFileTypeInteractor interactor;
    private final Subscriber subscriber;
    private final AppResources appResources;

    private String fileType;

    private final ObservableList<AuroraFile> foldersStack = new ObservableArrayList<>();
    private final Mapper<ReplaceFileViewModel, AuroraFile> mapper = new FileMapper(((position, item) -> onFileClicked(item)));

    private final OptionalDisposable reloadDisposable = new OptionalDisposable();
    private final OptionalDisposable popListenerDisposable = new OptionalDisposable();
    private final OptionalDisposable createFolderListenerDisposable = new OptionalDisposable();

    @Inject
    ReplaceFileTypeViewModel(ReplaceFileTypeInteractor interactor, Subscriber subscriber, AppResources appResources) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.appResources = appResources;

        SimpleOnListChangedCallback.addTo(foldersStack, list -> {
            interactor.notifyStackChanged(fileType, list.size());
            interactor.notifyCurrentFolderChanged(fileType, list.size() > 0 ? list.get(0) : null);
        });
    }

    public void setArgs(ReplaceFileTypeArgs args) {
        fileType = args.getType();

        popListenerDisposable.disposeAndClear();

        interactor.listenPopRequest(fileType)
                .compose(popListenerDisposable::track)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber.subscribe(type -> popStack()));

        interactor.listenCreateFolder(fileType)
                .compose(popListenerDisposable::track)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber.subscribe(type -> onCreateFolder()));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (!foldersStack.isEmpty()) {
            return;
        }

        foldersStack.add(AuroraFile.parse("", fileType, true));

        reloadCurrentFolder();
    }

    public void onRefresh() {
        reloadCurrentFolder();
    }

    private void reloadCurrentFolder() {
        reloadDisposable.disposeAndClear();
        items.clear();

        interactor.getFiles(foldersStack.get(0))
                .doOnSubscribe(disposable -> viewModelState.set(ViewModelState.LOADING))
                .doOnError(error -> viewModelState.set(ViewModelState.ERROR))
                .compose(reloadDisposable::track)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(this::handleFiles));
    }

    private void handleFiles(List<AuroraFile> files) {
        viewModelState.set(files.size() > 0 ? ViewModelState.CONTENT : ViewModelState.EMPTY);

        Stream.of(files)
                .filter(AuroraFile::isFolder)
                .map(mapper::map)
                .collect(StreamCollectors.setListByClearAdd(items));
    }

    private void onFileClicked(AuroraFile file) {
        if (file.isFolder()) {
            foldersStack.add(0, file);

            reloadCurrentFolder();
        }
    }

    private void popStack() {
        if (foldersStack.size() > 1) {
            foldersStack.remove(0);
            reloadCurrentFolder();
        }
    }

    private void onCreateFolder() {
        if (foldersStack.size() == 0) {
            return;
        }

        interactor.getCreateFolderName()
                .subscribe(subscriber.subscribe(this::createFolder));
    }

    private void createFolder(String name) {
        interactor.createFolder(name, foldersStack.get(0))
                .doOnSubscribe(disposable -> progress.set(ProgressViewModel.indeterminate(
                        appResources.getString(R.string.prompt_dialog_title_folder_cration),
                        name
                )))
                .doFinally(() -> progress.set(null))
                .compose(subscriber::defaultSchedulers)
                .subscribe(this::reloadCurrentFolder);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        popListenerDisposable.disposeAndClear();
        createFolderListenerDisposable.disposeAndClear();
        reloadDisposable.disposeAndClear();
    }
}
