package com.afterlogic.aurora.drive.presentation.modules.replace.interactor

import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.SearchableFilesListInteractor

import javax.inject.Inject

import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceFileTypeInteractor @Inject
internal constructor(
        private val filesRepository: FilesRepository,
        private val viewInteractor: ReplaceViewInteractor
) : SearchableFilesListInteractor(filesRepository) {

    val createFolderName: Maybe<String>
        get() = viewInteractor.folderName

    fun createFolder(name: String, currentFolder: AuroraFile): Single<AuroraFile> {
        val newFolder = AuroraFile.create(currentFolder, name, true)
        return filesRepository.createFolder(newFolder)
                .andThen(Single.just(newFolder))
    }

}
