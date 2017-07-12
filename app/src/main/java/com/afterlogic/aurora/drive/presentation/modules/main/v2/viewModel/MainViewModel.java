package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.SearchableFilesRootViewModel;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.ViewModelsConnection;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor.MainInteractor;
import com.annimon.stream.Stream;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainViewModel extends SearchableFilesRootViewModel<MainFilesListViewModel> {

    public final ObservableBoolean showBackButton = new ObservableBoolean(false);
    public final ObservableField<String> logoutButtonText = new ObservableField<>();

    private final MainInteractor interactor;
    private final Subscriber subscriber;
    private final Router router;

    private DisposableBag disposableBag = new DisposableBag();

    @Inject
    MainViewModel(MainInteractor interactor,
                  Subscriber subscriber,
                  Router router,
                  AppResources appResources,
                  ViewModelsConnection<MainFilesListViewModel> viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;

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

    private void updateBackButtonVisibility() {
        boolean show = Stream.of(fileTypesLocked.get(), showSearch.get())
                .anyMatch(any -> any);
        showBackButton.set(show);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposableBag.dispose();
    }
}
