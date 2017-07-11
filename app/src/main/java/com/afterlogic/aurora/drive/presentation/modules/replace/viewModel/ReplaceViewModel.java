package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

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
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceInteractor;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceArgs;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceViewModel extends LifecycleViewModel implements ViewModelsConnection.OnChangedListener {

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> subtitle = new UiObservableField<>();
    public final ObservableBoolean fileTypesLocked = new ObservableBoolean(false);
    public final ObservableList<FileType> fileTypes = new ObservableArrayList<>();
    public final Bindable<Integer> currentFileTypePosition = Bindable.create(0);

    public final ObservableField<ViewModelState> viewModelState = new UiObservableField<>(ViewModelState.LOADING);
    public final ObservableField<ProgressViewModel> progress = new UiObservableField<>(null);

    private final ReplaceInteractor interactor;
    private final Subscriber subscriber;
    private final Router router;
    private final AppResources appResources;
    private final ViewModelsConnection viewModelsConnection;

    private final OptionalDisposable loadingDisposable = new OptionalDisposable();

    private SimpleOnListChangedCallback<ObservableList<AuroraFile>> stackChangeListener = new SimpleOnListChangedCallback<>(this::onStackChanged);

    private boolean isCopyMode = false;
    private List<AuroraFile> filesForAction = null;

    @Inject
    ReplaceViewModel(ReplaceInteractor interactor,
                     Subscriber subscriber,
                     Router router,
                     AppResources appResources,
                     ViewModelsConnection viewModelsConnection) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;
        this.appResources = appResources;
        this.viewModelsConnection = viewModelsConnection;

        viewModelsConnection.setListener(this);

        startLoad();
    }

    @Override
    public void onRegistered(String type, ReplaceFileTypeViewModel vm) {
        vm.foldersStack.addOnListChangedCallback(stackChangeListener);
    }

    @Override
    public void onUnregistered(ReplaceFileTypeViewModel vm) {
        vm.foldersStack.removeOnListChangedCallback(stackChangeListener);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        viewModelsConnection.setListener(null);
    }

    public void setArgs(ReplaceArgs args) {
        filesForAction = args.getFiles();

        isCopyMode = args.isCopyMode();
        int titleId = isCopyMode ? R.string.prompt_title__copy : R.string.prompt_title__replace;
        title.set(appResources.getString(titleId));
    }

    public void onBackPressed() {
        if (fileTypesLocked.get() && getCurrentFileType() != null) {
            ReplaceFileTypeViewModel vm = viewModelsConnection.get(getCurrentFileType());
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

    public void onCreateFolder() {
        ReplaceFileTypeViewModel vm = viewModelsConnection.get(getCurrentFileType());
        if (vm != null) {
            vm.onCreateFolder();
        }
    }

    public void onPasteAction() {
        ReplaceFileTypeViewModel vm = viewModelsConnection.get(getCurrentFileType());
        if (vm != null) {
            AuroraFile topFolder = vm.foldersStack.get(0);
            Completable action;
            if (isCopyMode) {
                action =  interactor.copyFiles(topFolder, filesForAction);
            } else {
                action = interactor.replaceFiles(topFolder, filesForAction);
            }
            action.doOnSubscribe(disposable -> progress.set(createProgressViewModel()))
                    .doFinally(() -> progress.set(null))
                    .compose(subscriber::defaultSchedulers)
                    .subscribe(subscriber.subscribe(router::exit));
        }
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
        viewModelState.set(fileTypes.size() > 0 ? ViewModelState.CONTENT : ViewModelState.EMPTY);

        this.fileTypes.addAll(fileTypes);
    }

    private void onStackChanged(List<AuroraFile> files) {
        if (files.size() > 1) {
            fileTypesLocked.set(true);
            AuroraFile topFolder = files.get(0);
            subtitle.set(topFolder.getName());
        } else {
            fileTypesLocked.set(false);
            subtitle.set(null);
        }
    }

    private String getCurrentFileType() {
        return fileTypes.get(currentFileTypePosition.get()).getFilesType();
    }

    private ProgressViewModel createProgressViewModel() {
        int size = filesForAction.size();
        return ProgressViewModel.indeterminate(
                isCopyMode ? appResources.getString(R.string.prompt_coping) : appResources.getString(R.string.prompt_replacing),
                size == 1 ? filesForAction.get(0).getName() : appResources.getPlurals(R.plurals.prompt_files_count, size, size)
        );
    }
}
