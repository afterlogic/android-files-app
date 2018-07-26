package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.interactor

import android.net.Uri

import com.afterlogic.aurora.drive.core.common.logging.MyLog
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler
import com.afterlogic.aurora.drive.core.common.util.FileUtil
import com.afterlogic.aurora.drive.data.common.multiapi.MultiApiService
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.model.Progressible
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor

import java.io.File
import java.io.IOException

import javax.inject.Inject
import javax.inject.Named

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by sashka on 14.02.17.
 *
 *
 * mail: sunnyday.development@gmail.com
 */

class SyncInteractorImpl @Inject internal constructor(
        scheduler: ObservableScheduler,
        private val filesRepository: MultiApiService<FilesRepository>,
        @Named(FilesDataModule.OFFLINE_DIR) private val mOfflineFolder: File
) : BaseInteractor(scheduler), SyncInteractor {

    override fun getOfflineFiles(): Single<List<AuroraFile>> =
            filesRepository.single { offlineFiles }
                    .compose { composeImmediate(it) }

    override fun check(file: AuroraFile): Single<AuroraFile> =
            filesRepository.single { checkFile(file) }

    override fun upload(file: AuroraFile)
            : Observable<Progressible<AuroraFile>> = Observable.defer {

        val realFile = FileUtil.getFile(mOfflineFolder, file)

        filesRepository.observable { rewriteFile(file, Uri.fromFile(realFile)) }
                .doOnNext { progress ->
                    if (progress.isDone) {
                        if (!realFile.setLastModified(progress.data.lastModified)) {
                            MyLog.majorException(IOException("Can't set last modified"))
                        }
                    }
                }

    }

    override fun download(file: AuroraFile)
            : Observable<Progressible<AuroraFile>> = Observable.defer {

        val realFile = FileUtil.getFile(mOfflineFolder, file)

        filesRepository.observable { downloadOrGetOffline(file, realFile) }
                .map { it.map(file) }

    }

    override fun removeOffline(file: AuroraFile): Completable =
            filesRepository.completable { setOffline(file, false) }

}
