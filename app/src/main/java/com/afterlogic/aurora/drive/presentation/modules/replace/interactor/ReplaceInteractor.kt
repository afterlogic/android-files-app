package com.afterlogic.aurora.drive.presentation.modules.replace.interactor

import com.afterlogic.aurora.drive.data.common.multiapi.MultiApiService
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.FilesRootInteractor

import javax.inject.Inject

import io.reactivex.Completable

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceInteractor @Inject
internal constructor(private val filesRepository: MultiApiService<FilesRepository>) : FilesRootInteractor(filesRepository) {

    fun replaceFiles(targetFolder: AuroraFile, source: List<AuroraFile>): Completable {
        return filesRepository.completable { replaceFiles(targetFolder, source) }
    }

    fun copyFiles(targetFolder: AuroraFile, source: List<AuroraFile>): Completable {
        return filesRepository.completable { copyFiles(targetFolder, source) }
    }

}
