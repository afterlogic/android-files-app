package com.afterlogic.aurora.drive.presentation.modules.main.interactor

import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.core.common.contextWrappers.ClipboardHelper
import com.afterlogic.aurora.drive.core.common.util.FileUtil
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.model.Progressible
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.PermissionRequest
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.PermissionsInteractor
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.SearchableFilesListInteractor
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.rx.WakeLockTransformer
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncListener
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress

import java.io.File

import javax.inject.Inject
import javax.inject.Named

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

import android.content.Context.DOWNLOAD_SERVICE
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

class MainFilesListInteractor @Inject
internal constructor(private val filesRepository: FilesRepository,
                     private val clipboardHelper: ClipboardHelper,
                     private val wakeLockFactory: WakeLockTransformer.Factory,
                     private val appContext: Context,
                     @Named(FilesDataModule.CACHE_DIR) private val cacheDir: File,
                     @Named(FilesDataModule.DOWNLOADS_DIR) private val downloadsDir: File,
                     private val viewInteractor: MainViewInteractor,
                     private val permissionsInteractor: PermissionsInteractor) : SearchableFilesListInteractor(filesRepository) {

    private val syncListener: SyncListener = SyncListener(appContext)

    @Volatile
    private var syncObservablesCount = 0

    val syncProgress: Observable<SyncProgress>
        get() = syncListener.progressSource
                .doOnSubscribe {
                    if (syncObservablesCount == 0) {
                        syncListener.onStart()
                    }
                    syncObservablesCount++
                }
                .doFinally {
                    syncObservablesCount--
                    if (syncObservablesCount == 0) {
                        syncListener.onStop()
                    }
                }

    val newFolderName: Maybe<String>
        get() = viewInteractor.newFolderName

    val fileForUpload: Single<Uri>
        get() = viewInteractor.fileForUpload

    val networkState: Single<Boolean>
        get() = Single.fromCallable {

            val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as? ConnectivityManager ?: return@fromCallable false

            val activeNetwork = cm.activeNetworkInfo

            activeNetwork != null && activeNetwork.isConnectedOrConnecting

        }

    fun getThumbnail(file: AuroraFile): Single<Uri> {
        return filesRepository.getFileThumbnail(file)
    }

    fun getOfflineStatus(file: AuroraFile): Single<Boolean> {
        return filesRepository.getOfflineStatus(file)
    }

    fun downloadForOpen(file: AuroraFile): Observable<Progressible<File>> {
        val target = FileUtil.getFile(cacheDir, file)
        return filesRepository.downloadOrGetOffline(file, target)
                .startWith(Observable.just(Progressible<File>(null, 0, 0, file.name, false)))
                .compose { this.prepareLoadTask(it) }
    }

    fun createNewFolder(name: String, parentFolder: AuroraFile): Single<AuroraFile> {
        return Single.defer {
            val newFolder = AuroraFile.create(parentFolder, name, true)

            filesRepository.createFolder(newFolder)
                    .andThen(Single.just(newFolder))
        }
    }

    fun uploadFile(folder: AuroraFile, file: Uri): Observable<Progressible<AuroraFile>> {
        return filesRepository.uploadFile(folder, file)
                .compose { this.prepareLoadTask(it) }
    }

    fun getNewFileName(file: AuroraFile): Maybe<String> {
        return viewInteractor.getNewNameForFile(file)
    }

    fun rename(file: AuroraFile, newName: String): Single<AuroraFile> {
        return filesRepository.rename(file, newName)
    }

    fun deleteFile(file: AuroraFile): Completable {
        return filesRepository.delete(file)
    }

    fun downloadToDownloads(file: AuroraFile): Observable<Progressible<File>> {
        val target = FileUtil.getFile(downloadsDir, file)
        return filesRepository.downloadOrGetOffline(file, target)
                .compose { prepareLoadTask(it) }
                .doOnNext { progress ->
                    if (progress.isDone) {
                        val dm = appContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        dm.addCompletedDownload(
                                target.name,
                                appContext.getString(R.string.prompt_downloaded_file_description),
                                true,
                                FileUtil.getFileMimeType(target),
                                target.absolutePath,
                                target.length(),
                                false
                        )
                    }
                }
    }

    fun setOffline(file: AuroraFile, offline: Boolean): Completable {
        return filesRepository.setOffline(file, offline)
                .andThen(Completable.fromAction {
                    // Start sync
                    if (offline) {
                        SyncService.requestSync(file, appContext)
                    }
                })
    }

    fun createPublicLink(file: AuroraFile): Completable {
        return filesRepository.createPublicLink(file)
                .doOnSuccess { clipboardHelper.put(it) }
                .ignoreElement()
    }

    fun deletePublicLink(file: AuroraFile): Completable {
        return filesRepository.deletePublicLink(file)
    }

    private fun <T> prepareLoadTask(upstream: Observable<T>): Observable<T> {
        return upstream.startWith(
                permissionsInteractor.requirePermission(PermissionRequest.READ_AND_WRITE_STORAGE)
                        .observeOn(Schedulers.io())
                        .ignoreElement()
                        .toObservable()
        )//-----|
                .compose(wakeLockFactory.create())
    }

}
