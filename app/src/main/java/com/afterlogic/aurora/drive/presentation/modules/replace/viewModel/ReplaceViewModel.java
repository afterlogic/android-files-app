package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.core.common.streams.StreamCollectors;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceInteractor;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceArgs;
import com.annimon.stream.Stream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceViewModel extends BaseViewModel {

    public ObservableField<String> title = new ObservableField<>();
    public ObservableBoolean fileTypesLocked = new ObservableBoolean(false);
    public ObservableList<ReplaceFileTypeViewModel> fileTypes = new ObservableArrayList<>();
    public ObservableInt currentFileTypePosition = new ObservableInt(-1);

    public ObservableField<ViewModelState> viewModelState = new UiObservableField<>(ViewModelState.LOADING);

    private final ReplaceInteractor interactor;
    private final Subscriber subscriber;
    private final Router router;
    private final AppResources appResources;

    private final Provider<ReplaceFileTypeViewModel> viewModelProvider;
    private final OptionalDisposable loadingDisposable = new OptionalDisposable();

    private ReplaceFileTypeViewModel lockedViewModel = null;
    private final Map<String, ReplaceFileTypeViewModel> typesMap = new HashMap<>();

    @Inject
    ReplaceViewModel(ReplaceInteractor interactor, Subscriber subscriber, Router router, AppResources appResources, Provider<ReplaceFileTypeViewModel> viewModelProvider) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;
        this.appResources = appResources;
        this.viewModelProvider = viewModelProvider;

        startLoad();
    }

    public void setArgs(ReplaceArgs args) {
        int titleId = args.isCopyMode() ? R.string.prompt_action__copy : R.string.prompt_action__replace;
        title.set(appResources.getString(titleId));
    }

    public void onBackPressed() {
        if (fileTypesLocked.get() && lockedViewModel != null) {
            lockedViewModel.popStack();
        } else {
            router.exit();
        }
    }

    public void onRefresh() {
        fileTypes.clear();
        startLoad();
    }

    private void startLoad() {
        loadingDisposable.disposeAndClear();
        fileTypes.clear();
        typesMap.clear();

        interactor.getAvailableFileTypes()
                .doOnSubscribe(disposable -> viewModelState.set(ViewModelState.LOADING))
                .doOnError(error -> viewModelState.set(ViewModelState.ERROR))
                .compose(loadingDisposable::track)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(this::handleFileTypes));
    }

    private void handleFileTypes(List<FileType> fileTypes) {
        viewModelState.set(fileTypes.size() > 0 ? ViewModelState.CONTENT : ViewModelState.EMPTY);

        Stream.of(fileTypes)
                .map(type -> {
                    ReplaceFileTypeViewModel typeVM = viewModelProvider.get();
                    typeVM.setFileType(type);
                    SimpleOnPropertyChangedCallback.addTo(
                            typeVM.stackSize,
                            () -> onStackChanged(type.getFilesType(), typeVM.stackSize.get())
                    );

                    typesMap.put(type.getFilesType(), typeVM);
                    return typeVM;
                })
                .collect(StreamCollectors.setListByClearAdd(this.fileTypes));
    }

    private void onStackChanged(String type, int size) {
        if (size > 1) {
            fileTypesLocked.set(true);
            lockedViewModel = typesMap.get(type);
        } else {
            lockedViewModel = null;
            fileTypesLocked.set(false);
        }
    }
}
