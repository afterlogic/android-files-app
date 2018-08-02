package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.application.navigation.AppRouter
import com.afterlogic.aurora.drive.core.common.rx.Subscriber
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.model.Storage
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.AsyncUiObservableField
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.SearchableFilesRootViewModel
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.ViewModelsConnection
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceInteractor
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceArgs

import javax.inject.Inject

import ru.terrakok.cicerone.Router
import javax.inject.Provider

/**
 * Created by aleksandrcikin on 29.06.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceViewModel @Inject internal constructor(
        private val interactor: ReplaceInteractor,
        private val subscriber: Subscriber,
        private val router: Router,
        private val appResources: AppResources,
        private val args: Provider<ReplaceArgs>,
        private val viewModelsConnection: ViewModelsConnection<ReplaceFileListViewModel>
) : SearchableFilesRootViewModel<ReplaceFileListViewModel>(
        interactor, subscriber, router, appResources, viewModelsConnection) {

    val subtitle: ObservableField<String> = AsyncUiObservableField()

    private val isCopyMode by lazy { args.get().isCopyMode }

    private val filesForAction: List<AuroraFile> by lazy { args.get().files }

    init {
        setHasFixedTitle(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    internal fun onViewCreated() {

        val titleId = if (isCopyMode) R.string.prompt_title__copy else R.string.prompt_title__move
        title.set(appResources.getString(titleId))

    }

    override fun handleFileTypes(storages: MutableList<Storage>) {
        super.handleFileTypes(storages)

        filesForAction.map { it.type }
                .distinct()
                .singleOrNull()
                ?.let { type -> storages.find { it.type == type } }
                ?.let(storages::indexOf)
                ?.let(currentFileTypePosition::set)

    }

    override fun onExit() {
        router.exitWithResult(AppRouter.RESULT_CODE_REPLACE, null)
    }

    fun onCreateFolder() {
        viewModelsConnection.get(currentFileType)?.onCreateFolder()
    }

    fun onPasteAction() {

        val vm = viewModelsConnection.get(currentFileType) ?: return

        val topFolder = vm.foldersStack[0]

        val action = if (isCopyMode) interactor.copyFiles(topFolder, filesForAction)
        else interactor.replaceFiles(topFolder, filesForAction)

        action.doOnSubscribe { progress.set(createProgressViewModel()) }
                .doFinally { progress.set(null) }
                .compose { subscriber.defaultSchedulers(it) }
                .subscribe(subscriber.subscribe<Any>(Runnable {
                    router.exitWithResult(AppRouter.RESULT_CODE_REPLACE, topFolder)
                }))

    }

    override fun onStackChanged(files: List<AuroraFile>) {
        super.onStackChanged(files)

        if (files.size > 1) {
            val topFolder = files[0]
            subtitle.set(topFolder.fullPath)
        } else {
            subtitle.set(null)
        }

    }

    private fun createProgressViewModel(): ProgressViewModel {

        val size = filesForAction.size

        return ProgressViewModel.Factory.indeterminateCircle(
                if (isCopyMode) appResources.getString(R.string.prompt_coping) else appResources.getString(R.string.prompt_replacing),
                if (size == 1) filesForAction[0].name
                else appResources.getPlurals(R.plurals.prompt_files_count, size, size)
        )

    }

}
