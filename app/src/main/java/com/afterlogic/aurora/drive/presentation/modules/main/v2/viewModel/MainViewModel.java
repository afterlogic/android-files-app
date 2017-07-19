package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag;
import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.BaseFilesRootViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor.MainInteractor;
import com.annimon.stream.Stream;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import ru.terrakok.cicerone.Router;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainViewModel extends BaseFilesRootViewModel<MainFilesListViewModel> {

    public final Bindable<String> searchQuery = Bindable.create("");
    public final Bindable<Boolean> showSearch = Bindable.create(false);
    public final ObservableBoolean showBackButton = new ObservableBoolean(false);
    public final ObservableField<String> logoutButtonText = new ObservableField<>();

    private final MainInteractor interactor;
    private final Subscriber subscriber;
    private final MainViewModelsConnection viewModels;
    private final Router router;

    private DisposableBag disposableBag = new DisposableBag();

    @Inject
    MainViewModel(MainInteractor interactor,
                  Subscriber subscriber,
                  Router router,
                  AppResources appResources,
                  MainViewModelsConnection viewModelsConnection) {
        super(interactor, subscriber, router, appResources, viewModelsConnection);
        this.viewModels = viewModelsConnection;
        this.interactor = interactor;
        this.subscriber = subscriber;
        this.router = router;

        SimpleOnPropertyChangedCallback.addTo(searchQuery, this::onSearchQueryChanged);
        SimpleOnPropertyChangedCallback.addTo(showSearch, this::onShowSearchChanged);

        SimpleOnPropertyChangedCallback.addTo(
                this::updateBackButtonVisibility,
                showSearch, fileTypesLocked
        );

        viewModelsConnection.fileClickedPublisher
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFileClicked);

        interactor.getUserLogin()
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(login -> {
                    String text = appResources.getString(R.string.logout, login);
                    logoutButtonText.set(text);
                }));
    }

    @Override
    public void onBackPressed() {
        if (showSearch.get()) {
            showSearch.set(false);
        } else {
            super.onBackPressed();
        }
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

    private void onSearchQueryChanged() {
        if (fileTypesLocked.get()) {
            setSearchQueryForType(getCurrentFileType());
        } else {
            Stream.of(fileTypes)
                    .map(FileType::getFilesType)
                    .forEach(this::setSearchQueryForType);
        }
    }

    private void setSearchQueryForType(String type) {
        MainFilesListViewModel vm = viewModels.get(type);
        if (vm != null) {
            vm.onSearchQuery(searchQuery.get());
        }
    }

    private void onShowSearchChanged() {
        if (!showSearch.get()) {
            searchQuery.set("");
        }
    }

    private void updateBackButtonVisibility() {
        boolean show = Stream.of(fileTypesLocked.get(), showSearch.get())
                .anyMatch(any -> any);
        showBackButton.set(show);
    }

    private void onFileClicked(AuroraFile file) {
        showSearch.set(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposableBag.dispose();
    }
}
