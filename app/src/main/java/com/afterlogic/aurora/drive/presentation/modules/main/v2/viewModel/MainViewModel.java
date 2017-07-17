package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.RxVariable;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFilesRootViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor.MainInteractor;
import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

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
    public final ObservableBoolean multiChoiceHasFolder = new ObservableBoolean(false);

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

        viewModelsConnection.getMultiChoiceCount()
                .compose(disposableBag::track)
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(this::handleMultiChoice));

        SimpleOnPropertyChangedCallback.addTo(
                multiChoiceMode,
                field -> viewModelsConnection.setMultiChoiceMode(field.get())
        );
    }

    public void onLogout() {
        interactor.logout()
                .compose(subscriber::defaultSchedulers)
                .subscribe(() -> router.newRootScreen(AppRouter.LOGIN));
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

    private void handleMultiChoice(RxVariable.Value<List<AuroraFile>> multiChoice) {
        List<AuroraFile> list = multiChoice.get();
        if (list == null) return;

        multiChoiceCount.set(list.size());
        multiChoiceHasFolder.set(Stream.of(list).anyMatch(AuroraFile::isFolder));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposableBag.dispose();
    }
}
