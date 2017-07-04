package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.core.common.streams.StreamCollectors;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnListChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceFileTypeInteractor;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeViewModel extends BaseViewModel {

    public final ObservableField<ViewModelState> viewModelState = new UiObservableField<>();
    public final ObservableList<ReplaceFileViewModel> items = new ObservableArrayList<>();
    public final ObservableInt stackSize = new ObservableInt(0);

    private final ReplaceFileTypeInteractor interactor;
    private final Subscriber subscriber;

    public FileType fileType;

    private final ObservableList<AuroraFile> foldersStack = new ObservableArrayList<>();
    private final Mapper<ReplaceFileViewModel, AuroraFile> mapper = new FileMapper(((position, item) -> onFileClicked(item)));

    private final OptionalDisposable reloadDisposable = new OptionalDisposable();

    @Inject
    ReplaceFileTypeViewModel(ReplaceFileTypeInteractor interactor, Subscriber subscriber) {
        this.interactor = interactor;
        this.subscriber = subscriber;

        SimpleOnListChangedCallback.addTo(foldersStack, list -> stackSize.set(list.size()));
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void popStack() {
        if (foldersStack.size() > 1) {
            foldersStack.remove(0);
            reloadCurrentFolder();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (!foldersStack.isEmpty()) {
            return;
        }

        foldersStack.add(AuroraFile.parse("", fileType.getFilesType(), true));

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
}
