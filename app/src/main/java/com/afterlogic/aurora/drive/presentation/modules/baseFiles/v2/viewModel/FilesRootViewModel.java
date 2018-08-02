package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Storage;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnListChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.AsyncUiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.SynchronizedUiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.FilesRootInteractor;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FilesRootViewModel<
        FileListVM extends FileListViewModel<FileListVM, ?, ?>
> extends LifecycleViewModel implements ViewModelsConnection.OnChangedListener<FileListVM> {

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableBoolean fileTypesLocked = new ObservableBoolean(false);
    public final ObservableList<Storage> storages = new ObservableArrayList<>();
    public final Bindable<Integer> currentFileTypePosition = Bindable.create(0);

    public final ObservableField<ViewModelState> viewModelState = new SynchronizedUiObservableField<>(ViewModelState.LOADING);
    public final ObservableField<ProgressViewModel> progress = new AsyncUiObservableField<>(null);

    private boolean hasFixedTitle = false;

    private final FilesRootInteractor interactor;
    private final Subscriber subscriber;
    private final Router router;
    private final AppResources appResources;
    private final ViewModelsConnection<FileListVM> viewModelsConnection;

    private final OptionalDisposable loadingDisposable = new OptionalDisposable();
    private final OptionalDisposable fileClickDisposable = new OptionalDisposable();

    private SimpleOnListChangedCallback<ObservableList<AuroraFile>> stackChangeListener = new SimpleOnListChangedCallback<>(this::onStackChanged);

    protected FilesRootViewModel(FilesRootInteractor interactor,
                                 Subscriber subscriber,
                                 Router router,
                                 AppResources appResources,
                                 ViewModelsConnection<FileListVM> viewModelsConnection) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;
        this.appResources = appResources;
        this.viewModelsConnection = viewModelsConnection;

        viewModelsConnection.setListener(this);

        viewModelsConnection.fileClickedPublisher
                .observeOn(AndroidSchedulers.mainThread())
                .compose(fileClickDisposable::track)
                .subscribe(subscriber.subscribe(this::onFileClicked));

        reloadFileTypes();

        title.set(getRootTitle());
    }

    @Override
    public void onRegistered(String type, FileListVM vm) {
        vm.foldersStack.addOnListChangedCallback(stackChangeListener);
    }

    @Override
    public void onUnregistered(FileListVM vm) {
        vm.foldersStack.removeOnListChangedCallback(stackChangeListener);
    }

    public void onBackPressed() {

        if (fileTypesLocked.get() && getCurrentFileType() != null) {
            FileListVM vm = viewModelsConnection.get(getCurrentFileType());
            if (vm != null) {
                vm.onPopFolder();
                return;
            }
        }

        onExit();

    }

    protected void onExit() {
        router.exit();
    }

    public void onRefresh() {
        reloadFileTypes();
    }

    protected void setHasFixedTitle(boolean hasFixedTitle) {
        this.hasFixedTitle = hasFixedTitle;
    }

    protected void onStackChanged(List<AuroraFile> files) {
        if (files.size() > 1) {
            if (!hasFixedTitle) {
                AuroraFile topFolder = files.get(0);
                title.set(topFolder.getName());
            }
            fileTypesLocked.set(true);
        } else {
            if (!hasFixedTitle) {
                title.set(getRootTitle());
            }
            fileTypesLocked.set(false);
        }
    }

    protected String getCurrentFileType() {
        return storages.get(currentFileTypePosition.get()).getType();
    }

    protected String getRootTitle() {
        return appResources.getString(R.string.app_name);
    }


    protected void onFileClicked(AuroraFile file) {
       //no-op
    }

    private void reloadFileTypes() {
        viewModelState.set(ViewModelState.LOADING);

        storages.clear();

        interactor.getAvailableFileTypes()
                .doOnError(error -> viewModelState.set(ViewModelState.ERROR))
                .compose(subscriber::defaultSchedulers)
                .compose(loadingDisposable::disposeAndTrack)
                .subscribe(subscriber.subscribe(this::handleFileTypes));
    }

    protected void handleFileTypes(List<Storage> storages) {
        this.storages.addAll(storages);
        viewModelState.set(this.storages.size() > 0 ? ViewModelState.CONTENT : ViewModelState.EMPTY);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        viewModelsConnection.setListener(null);
        fileClickDisposable.disposeAndClear();
    }

}
