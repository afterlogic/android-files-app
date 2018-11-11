package com.afterlogic.aurora.drive.presentation.modules.fileView.interactor

import android.net.Uri
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by sashka on 16.02.17.
 *
 *
 * mail: sunnyday.development@gmail.com
 */

class FileViewImageItemInteractorImpl @Inject internal constructor(
        scheduler: ObservableScheduler,
        private val mFilesRepository: FilesRepository
) : BaseInteractor(scheduler), FileViewImageItemInteractor {

    override fun viewFile(file: AuroraFile): Single<Uri> {
        return mFilesRepository.viewFile(file)
                .compose { composeDefault(it) }
    }

}
