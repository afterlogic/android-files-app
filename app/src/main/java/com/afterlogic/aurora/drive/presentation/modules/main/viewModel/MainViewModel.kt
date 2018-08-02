package com.afterlogic.aurora.drive.presentation.modules.main.viewModel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.application.navigation.AppRouter
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer
import com.afterlogic.aurora.drive.core.common.rx.DisposableBag
import com.afterlogic.aurora.drive.core.common.rx.Subscriber
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.ViewModelState
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.SearchableFilesRootViewModel
import com.afterlogic.aurora.drive.presentation.modules.main.interactor.MainInteractor
import com.annimon.stream.Stream

import javax.inject.Inject

import io.reactivex.android.schedulers.AndroidSchedulers
import ru.terrakok.cicerone.Router

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

class MainViewModel @Inject internal constructor(
        private val interactor: MainInteractor,
        private val subscriber: Subscriber,
        private val router: Router,
        appResources: AppResources,
        private val viewModelsConnection: MainViewModelsConnection
) : SearchableFilesRootViewModel<MainFilesListViewModel>(
        interactor, subscriber, router, appResources, viewModelsConnection) {

    val showBackButton = ObservableBoolean(false)
    val logoutButtonText = ObservableField<String>()
    val multiChoiceMode = ObservableBoolean()
    val multiChoiceCount = ObservableInt(0)
    val multiChoiceDownloadable = ObservableBoolean(false)
    val multiChoiceOfflineEnabled = ObservableBoolean(false)

    private val disposableBag = DisposableBag()

    init {

        SimpleOnPropertyChangedCallback.addTo(
                Runnable { this.updateBackButtonVisibility() },
                showSearch, fileTypesLocked
        )

        interactor.userLogin
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe { login ->
                    val text = appResources.getString(R.string.logout, login)
                    logoutButtonText.set(text)
                })

        viewModelsConnection.getMultiChoice()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(disposableBag::track)
                .subscribe(subscriber.subscribe(Consumer { this.handleMultiChoice(it) }))

        SimpleOnPropertyChangedCallback.addTo(
                multiChoiceMode
        ) { field -> viewModelsConnection.setMultiChoiceMode(field.get()) }

        SimpleOnPropertyChangedCallback.addTo(viewModelState) { this.onViewModelStateChanged() }

        interactor.filesRepositoryResolved
                .subscribe(subscriber.subscribe { resoleved -> if (!resoleved) onLogout() })

    }

    private fun onViewModelStateChanged() {
        if (viewModelState.get() == ViewModelState.ERROR) {
            interactor.networkState
                    .subscribe(subscriber.subscribe { networkEnabled ->
                        if (!networkEnabled) {
                            router.newRootScreen(AppRouter.OFFLINE, false)
                        }
                    })
        }
    }

    fun onLogout() {

        interactor.logout()
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe<Any>( Runnable {
                    router.newRootScreen(AppRouter.LOGIN)
                }))

    }

    fun onOpenOfflineMode() {
        router.navigateTo(AppRouter.OFFLINE)
    }

    fun onOpenAbout() {
        router.navigateTo(AppRouter.ABOUT)
    }

    fun onMultiChoice() {
        multiChoiceMode.set(true)
    }

    fun onCreateFolderClick() {
        viewModelsConnection.sendMainAction(currentFileType, MainAction.CREATE_FOLDER)
    }

    fun onUploadFileClick() {
        viewModelsConnection.sendMainAction(currentFileType, MainAction.UPLOAD_FILE)
    }

    fun onMultiChoiceDelete() {
        onMultiChoiceAction(MultiChoiceAction.DELETE)
    }

    fun onMultiChoiceDownload() {
        onMultiChoiceAction(MultiChoiceAction.DOWNLOAD)
    }

    fun onMultiChoiceShare() {
        onMultiChoiceAction(MultiChoiceAction.SHARE)
    }

    fun onMultiChoiceReplace() {
        onMultiChoiceAction(MultiChoiceAction.REPLACE)
    }

    fun onMultiChoiceCopy() {
        onMultiChoiceAction(MultiChoiceAction.COPY)
    }

    fun onMultiChoiceOffline() {
        onMultiChoiceAction(MultiChoiceAction.TOGGLE_OFFLINE)
    }

    private fun onMultiChoiceAction(action: MultiChoiceAction) {
        viewModelsConnection.sendMultiChoiceAction(currentFileType, action)
        multiChoiceMode.set(false)
    }

    private fun updateBackButtonVisibility() {
        val show = Stream.of(fileTypesLocked.get(), showSearch.get())
                .anyMatch { any -> any }
        showBackButton.set(show)
    }

    private fun handleMultiChoice(list: List<MultiChoiceFile>) {

        multiChoiceCount.set(list.size)

        val downloadable = list.isNotEmpty() && list
                .map { it.file }
                .any { it.isFolder || it.isLink }
        multiChoiceDownloadable.set(downloadable)

        val offlineEnabled = list.isNotEmpty() && list.all { it.isOfflineEnabled }
        multiChoiceOfflineEnabled.set(offlineEnabled)

    }

    override fun onCleared() {
        super.onCleared()
        disposableBag.dispose()
    }
}
