package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnListChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.FilesRootInteractor;

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
    public final ObservableList<FileType> fileTypes = new ObservableArrayList<>();
    public final Bindable<Integer> currentFileTypePosition = Bindable.create(0);

    public final ObservableField<ViewModelState> viewModelState = new UiObservableField<>(ViewModelState.LOADING);
    public final ObservableField<ProgressViewModel> progress = new UiObservableField<>(null);

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
                .compose(fileClickDisposable::track)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFileClicked);

        startLoad();

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

        router.exit();
    }

    public void onRefresh() {
        fileTypes.clear();
        startLoad();
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
        return fileTypes.get(currentFileTypePosition.get()).getFilesType();
    }

    protected String getRootTitle() {
        return appResources.getString(R.string.app_name);
    }


    protected void onFileClicked(AuroraFile file) {
       //no-op
    }

    private void startLoad() {
        loadingDisposable.disposeAndClear();
        fileTypes.clear();

        interactor.getAvailableFileTypes()
                .doOnSubscribe(disposable -> viewModelState.set(ViewModelState.LOADING))
                .doOnError(error -> viewModelState.set(ViewModelState.ERROR))
                .compose(loadingDisposable::track)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(this::handleFileTypes));
    }

    private void handleFileTypes(List<FileType> fileTypes) {
        this.fileTypes.addAll(fileTypes);
        viewModelState.set(this.fileTypes.size() > 0 ? ViewModelState.CONTENT : ViewModelState.EMPTY);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        viewModelsConnection.setListener(null);
        fileClickDisposable.disposeAndClear();
    }
}
