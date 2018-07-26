package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor

import com.afterlogic.aurora.drive.data.common.multiapi.MultiApiService
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.model.Storage
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.InteractorUtil

import javax.inject.Inject

import io.reactivex.Single

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

open class FilesRootInteractor @Inject
constructor(private val filesRepository: MultiApiService<FilesRepository>) {

    open val availableFileTypes: Single<List<Storage>>
        get() = filesRepository.single { availableStorages }
                .compose { InteractorUtil.retryIfNotAuthError(3, it) }

}
