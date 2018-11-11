package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer
import com.afterlogic.aurora.drive.core.common.rx.Subscriber
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.view.FileListArgs
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.SearchableFileListViewModel
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.ViewModelsConnection
import com.afterlogic.aurora.drive.presentation.modules.replace.interactor.ReplaceFileTypeInteractor
import com.afterlogic.aurora.drive.presentation.modules.replace.view.ReplaceArgs
import com.annimon.stream.Stream

import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceFileListViewModel @Inject
internal constructor(
        private val args: Provider<ReplaceArgs>,
        private val interactor: ReplaceFileTypeInteractor,
        private val subscriber: Subscriber,
        private val appResources: AppResources,
        viewModelsConnection: ViewModelsConnection<ReplaceFileListViewModel>
) : SearchableFileListViewModel<ReplaceFileListViewModel, ReplaceFileViewModel, FileListArgs>(
        interactor, subscriber, viewModelsConnection) {

    private val excludePaths by lazy {
        args.get().files
                .filter { it.isFolder }
                .map { it.fullPath }
    }

    override fun mapFileItem(
            file: AuroraFile,
            onItemClickListener: OnActionListener<AuroraFile>
    ): ReplaceFileViewModel = ReplaceFileViewModel(file, onItemClickListener)

    override fun handleFiles(files: List<AuroraFile>) {
        val folders = Stream.of(files)
                .filter { it.isFolder && !excludePaths.contains(it.fullPath) }
                .toList()
        super.handleFiles(folders)
    }

    internal fun onCreateFolder() {

        if (foldersStack.isEmpty()) return

        interactor.createFolderName
                .subscribe(subscriber.subscribe(Consumer { this.createFolder(it) }))

    }

    private fun createFolder(name: String) {

        interactor.createFolder(name, foldersStack[0])
                .doOnSubscribe {
                    progress.set(ProgressViewModel.Factory.indeterminateCircle(
                            appResources.getString(R.string.prompt_dialog_title_folder_creation),
                            name
                    ))
                }
                .doFinally { progress.set(null) }
                .compose(subscriber::defaultSchedulers)
                .subscribe(subscriber.subscribe(Consumer { this.onFolderCreated(it) }))

    }

    private fun onFolderCreated(folder: AuroraFile) {
        foldersStack.add(0, folder)
    }

}
