package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.RxVariable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.SearchableFilesRootViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.interactor.MainInteractor;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainViewModel extends SearchableFilesRootViewModel<MainFilesListViewModel> {

    public final ObservableBoolean showBackButton = new ObservableBoolean(false);
    public final ObservableField<String> logoutButtonText = new ObservableField<>();
    public final ObservableBoolean multiChoiceMode = new ObservableBoolean();
    public final ObservableInt multiChoiceCount = new ObservableInt(0);
    public final ObservableBoolean multiChoiceDownloadable = new ObservableBoolean(false);
    public final ObservableBoolean multiChoiceOfflineEnabled = new ObservableBoolean(false);

    private final MainInteractor interactor;
    private final Subscriber subscriber;
    private final Router router;
    private final MainViewModelsConnection viewModelsConnection;

    private DisposableBag disposableBag = new DisposableBag();

    @Inject
    MainViewModel(MainInteractor interactor,
                  Subscriber subscriber,
                  Router router,
                  AppResources appResources,
                  MainViewModelsConnection viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;
        this.viewModelsConnection = viewModelsConnection;

        SimpleOnPropertyChangedCallback.addTo(
                this::updateBackButtonVisibility,
                showSearch, fileTypesLocked
        );

        interactor.getUserLogin()
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(login -> {
                    String text = appResources.getString(R.string.logout, login);
                    logoutButtonText.set(text);
                }));

        viewModelsConnection.getMultiChoice()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(disposableBag::track)
                .subscribe(subscriber.subscribe(this::handleMultiChoice));

        SimpleOnPropertyChangedCallback.addTo(
                multiChoiceMode,
                field -> viewModelsConnection.setMultiChoiceMode(field.get())
        );

        SimpleOnPropertyChangedCallback.addTo(viewModelState, this::onViewModelStateChanged);

        interactor.getFilesRepositoryResolved()
                .subscribe(subscriber.subscribe(resoleved -> {
                    if (!resoleved) onLogout();
                }));

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

    public void onLogout() {

        interactor.logout()
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(() -> router.newRootScreen(AppRouter.LOGIN)));

    }

    public void onOpenOfflineMode() {
        router.navigateTo(AppRouter.OFFLINE);
    }

    public void onOpenAbout() {
        router.navigateTo(AppRouter.ABOUT);
    }
    
    public void onMultiChoice() {
        multiChoiceMode.set(true);
    }
    
    public void onCreateFolderClick() {
        viewModelsConnection.sendMainAction(getCurrentFileType(), MainAction.CREATE_FOLDER);
    }
    
    public void onUploadFileClick() {
        viewModelsConnection.sendMainAction(getCurrentFileType(), MainAction.UPLOAD_FILE);
    }

    public void onMultiChoiceDelete() {
        onMultiChoiceAction(MultiChoiceAction.DELETE);
    }

    public void onMultiChoiceDownload() {
        onMultiChoiceAction(MultiChoiceAction.DOWNLOAD);
    }

    public void onMultiChoiceShare() {
        onMultiChoiceAction(MultiChoiceAction.SHARE);
    }

    public void onMultiChoiceReplace() {
        onMultiChoiceAction(MultiChoiceAction.REPLACE);
    }

    public void onMultiChoiceCopy() {
        onMultiChoiceAction(MultiChoiceAction.COPY);
    }

    public void onMultiChoiceOffline() {
        onMultiChoiceAction(MultiChoiceAction.TOGGLE_OFFLINE);
    }

    private void onMultiChoiceAction(MultiChoiceAction action) {
        viewModelsConnection.sendMultiChoiceAction(getCurrentFileType(), action);
        multiChoiceMode.set(false);
    }

    private void updateBackButtonVisibility() {
        boolean show = Stream.of(fileTypesLocked.get(), showSearch.get())
                .anyMatch(any -> any);
        showBackButton.set(show);
    }

    private void handleMultiChoice(RxVariable.Value<List<MultiChoiceFile>> multiChoice) {
        List<MultiChoiceFile> list = multiChoice.get();
        if (list == null) return;

        multiChoiceCount.set(list.size());

        boolean downloadable = !Stream.of(list)
                .map(MultiChoiceFile::getFile)
                .anyMatch(file -> file.isFolder() || file.isLink());
        multiChoiceDownloadable.set(downloadable);

        multiChoiceOfflineEnabled.set(Stream.of(list).allMatch(MultiChoiceFile::isOfflineEnabled));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposableBag.dispose();
    }
}
