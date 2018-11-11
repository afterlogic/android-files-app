package com.afterlogic.aurora.drive.presentation.modules.offline.interactor

import android.net.Uri

import com.afterlogic.aurora.drive.core.common.util.FileUtil
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.model.Progressible
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.SearchableFilesListInteractor
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncListener
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress

import java.io.File
import java.io.FileNotFoundException

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule.Companion.OFFLINE_DIR

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

class OfflineFileListInteractor @Inject internal constructor(
        private val filesRepository: FilesRepository,
        @Named(OFFLINE_DIR) private val offlineDir: File,
        private val syncListener: Provider<SyncListener>
) : SearchableFilesListInteractor(filesRepository) {

    override fun getFiles(folder: AuroraFile): Single<List<AuroraFile>> {
        return filesRepository.offlineFiles
    }

    override fun getFiles(folder: AuroraFile, pattern: String): Single<List<AuroraFile>> =
            getFiles(folder)
                    .map {
                        val preparedPattern = pattern.trim().toLowerCase()
                        it.filter {
                            val preparedName = it.name
                                    .trim()
                                    .toLowerCase()
                            preparedName.contains(preparedPattern)
                        }
                    }

    fun downloadForOpen(file: AuroraFile): Observable<Progressible<File>> = Observable.defer {
        val target = FileUtil.getFile(offlineDir, file)

        if (!target.exists()) {
            throw FileNotFoundException()
        }

        Observable.just(
                Progressible(target, target.length(), target.length(), file.name, true))
    }

    fun checkIsSynced(file: AuroraFile): Single<Boolean> = Single.fromCallable {
        FileUtil.getFile(offlineDir, file).exists()
    }

    fun disableOffline(file: AuroraFile): Completable = filesRepository.setOffline(file, false)

    fun getThumbnail(file: AuroraFile): Single<Uri> = Single.fromCallable {
        val localFile = File(offlineDir, file.pathSpec)
        Uri.fromFile(localFile)
    }

    fun listenSyncProgress(): Observable<SyncProgress> {

        val syncListener = syncListener.get()

        return Observable.defer { syncListener.progressSource }
                .doOnSubscribe { syncListener.onStart() }
                .doFinally { syncListener.onStop() }

    }

}
