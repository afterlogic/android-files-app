package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.core.common.streams.StreamCollectors;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnListChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.BaseFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.view.BaseFileListArgs;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public abstract class BaseFileListViewModel<
        FileListVM extends BaseFileListViewModel,
        FileVM extends BaseAuroraFileViewModel,
        Args extends BaseFileListArgs
> extends LifecycleViewModel {

    public final ObservableField<ViewModelState> viewModelState = new UiObservableField<>();
    public final ObservableList<FileVM> items = new ObservableArrayList<>();
    public final ObservableField<ProgressViewModel> progress = new UiObservableField<>(null);

    private final BaseFilesListInteractor interactor;
    private final Subscriber subscriber;
    private final ViewModelsConnection<FileListVM> viewModelsConnection;

    private String fileType;

    public final ObservableList<AuroraFile> foldersStack = new ObservableArrayList<>();

    private final OptionalDisposable reloadDisposable = new OptionalDisposable();
    private final OptionalDisposable popListenerDisposable = new OptionalDisposable();
    private final OptionalDisposable createFolderListenerDisposable = new OptionalDisposable();

    private final AtomicBoolean firstSetArgs = new AtomicBoolean(true);

    protected BaseFileListViewModel(BaseFilesListInteractor interactor,
                          Subscriber subscriber,
                          ViewModelsConnection<FileListVM> viewModelsConnection) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.viewModelsConnection = viewModelsConnection;

        SimpleOnListChangedCallback.addTo(foldersStack, stack -> onRefresh());
    }

    public void setArgs(Args args) {
        fileType = args.getType();

        if (firstSetArgs.getAndSet(false)) {
            viewModelsConnection.register(fileType, (FileListVM) this);
        }

        popListenerDisposable.disposeAndClear();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (!foldersStack.isEmpty()) {
            return;
        }

        foldersStack.add(AuroraFile.parse("", fileType, true));
    }

    public void onRefresh() {
        reloadCurrentFolder();
    }

    void onPopFolder() {
        if (foldersStack.size() > 1) {
            foldersStack.remove(0);
        }
    }

    protected String getFileType() {
        return getFileType();
    }

    protected void reloadCurrentFolder() {
        reloadDisposable.disposeAndClear();
        items.clear();

        if (foldersStack.size() == 0) return;


        getFilesSource(foldersStack.get(0))
                .doOnSubscribe(disposable -> viewModelState.set(ViewModelState.LOADING))
                .doOnError(error -> viewModelState.set(ViewModelState.ERROR))
                .compose(reloadDisposable::track)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(this::handleFiles));
    }

    protected Single<List<AuroraFile>> getFilesSource(AuroraFile folder) {
        return interactor.getFiles(folder);
    }

    protected void handleFiles(List<AuroraFile> files) {
        viewModelState.set(files.size() > 0 ? ViewModelState.CONTENT : ViewModelState.EMPTY);

        OnItemClickListener<AuroraFile> onItemClickListener = (p, file) -> onFileClicked(file);
        Stream.of(files)
                .map(file -> mapFileItem(file, onItemClickListener))
                .collect(StreamCollectors.setListByClearAdd(items));
    }

    protected abstract FileVM mapFileItem(AuroraFile file, OnItemClickListener<AuroraFile> onItemClickListener);

    protected void onFileClicked(AuroraFile file) {
        if (file.isFolder()) {
            foldersStack.add(0, file);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        popListenerDisposable.disposeAndClear();
        createFolderListenerDisposable.disposeAndClear();
        reloadDisposable.disposeAndClear();
        viewModelsConnection.unregister((FileListVM) this);
    }
}
