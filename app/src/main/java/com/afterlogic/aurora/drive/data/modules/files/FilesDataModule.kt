package com.afterlogic.aurora.drive.data.modules.files

import android.content.Context
import android.os.Environment

import com.afterlogic.aurora.drive.data.common.annotations.P7
import com.afterlogic.aurora.drive.data.common.annotations.P8
import com.afterlogic.aurora.drive.data.common.multiapi.MultiApiUtil
import com.afterlogic.aurora.drive.data.common.network.SessionManager
import com.afterlogic.aurora.drive.data.modules.files.mapper.general.FileMapperFactoryImpl
import com.afterlogic.aurora.drive.data.modules.files.mapper.general.FilesMapperFactory
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.data.modules.files.service.FilesLocalService
import com.afterlogic.aurora.drive.data.modules.files.service.FilesLocalServiceImpl

import java.io.File

import javax.inject.Named
import javax.inject.Provider

import dagger.Module
import dagger.Provides

/**
 * Created by sashka on 02.02.17.
 *
 *
 * mail: sunnyday.development@gmail.com
 */
@Module(includes = arrayOf(P7FilesDataModule::class, P8FilesDataModule::class))
class FilesDataModule {

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use MultiApiService.")
    @Provides
    internal fun repository(configurator: SessionManager,
                            @P7 p7: Provider<FilesRepository>,
                            @P8 p8: Provider<FilesRepository>): FilesRepository {

        return MultiApiUtil
                .chooseByApiVersion(configurator, p7, p8)

    }

    @Provides
    internal fun localService(service: FilesLocalServiceImpl): FilesLocalService {
        return service
    }

    @Provides
    internal fun mapperFactory(mapperFactory: FileMapperFactoryImpl): FilesMapperFactory {
        return mapperFactory
    }

    @Provides
    @Named(CACHE_DIR)
    internal fun cacheDir(appContext: Context): File {
        return if (Environment.isExternalStorageEmulated()) {
            File(appContext.externalCacheDir, "files/")
        } else {
            File(appContext.cacheDir, "files/")
        }
    }

    @Provides
    @Named(THUMB_DIR)
    internal fun thumbDir(appContext: Context): File {
        return if (Environment.isExternalStorageEmulated()) {
            File(appContext.externalCacheDir, "thumb/")
        } else {
            File(appContext.cacheDir, "thumb/")
        }
    }

    @Provides
    @Named(OFFLINE_DIR)
    internal fun offlineDir(appContext: Context): File {
        return if (Environment.isExternalStorageEmulated()) {
            appContext.getExternalFilesDir("offline")
        } else {
            File(appContext.filesDir, "offline/")
        }
    }

    @Provides
    @Named(DOWNLOADS_DIR)
    internal fun dowloadsDir(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    companion object {

        const val CACHE_DIR = "cache"
        const val THUMB_DIR = "thumb"
        const val OFFLINE_DIR = "offline"
        const val DOWNLOADS_DIR = "downloads"

    }

}
