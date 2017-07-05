package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.BaseViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceInteractor;
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceArgs;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceViewModel extends BaseViewModel {

    public ObservableField<String> title = new ObservableField<>();
    public ObservableBoolean fileTypesLocked = new ObservableBoolean(false);
    public ObservableList<FileType> fileTypes = new ObservableArrayList<>();
    public ObservableInt currentFileTypePosition = new ObservableInt(-1);

    public ObservableField<ViewModelState> viewModelState = new UiObservableField<>(ViewModelState.LOADING);

    private final ReplaceInteractor interactor;
    private final Subscriber subscriber;
    private final Router router;
    private final AppResources appResources;

    private final OptionalDisposable loadingDisposable = new OptionalDisposable();

    private String lockedViewModelType = null;

    @Inject
    ReplaceViewModel(ReplaceInteractor interactor, Subscriber subscriber, Router router, AppResources appResources) {
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;
        this.appResources = appResources;

        interactor.listenFoldersStack()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber.subscribe(this::handleStackChanges));

        startLoad();
    }

    public void setArgs(ReplaceArgs args) {
        int titleId = args.isCopyMode() ? R.string.prompt_title__copy : R.string.prompt_title__replace;
        title.set(appResources.getString(titleId));
    }

    public void onBackPressed() {
        if (fileTypesLocked.get() && lockedViewModelType != null) {
            interactor.popFolder(lockedViewModelType);
        } else {
            router.exit();
        }
    }

    public void onRefresh() {
        fileTypes.clear();
        startLoad();
    }

    public void onPasteAction() {

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

    private void handleStackChanges(FolderStackSize stackSize) {
        if (stackSize.getDepth() > 1) {
            fileTypesLocked.set(true);
            lockedViewModelType = stackSize.getType();
        } else {
            lockedViewModelType = null;
            fileTypesLocked.set(false);
        }
    }
}
