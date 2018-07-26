package com.afterlogic.aurora.drive.presentation.modules.main.interactor

import android.content.Context
import android.net.ConnectivityManager

import com.afterlogic.aurora.drive.core.common.util.AppUtil
import com.afterlogic.aurora.drive.core.consts.Const
import com.afterlogic.aurora.drive.data.common.multiapi.MultiApiService
import com.afterlogic.aurora.drive.data.common.network.SessionManager
import com.afterlogic.aurora.drive.data.modules.auth.AuthenticatorService
import com.afterlogic.aurora.drive.data.modules.cleaner.DataCleaner
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.FilesRootInteractor
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService

import javax.inject.Inject

import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

class MainInteractor @Inject
internal constructor(filesRepository: MultiApiService<FilesRepository>,
                     private val authenticatorService: AuthenticatorService,
                     private val dataCleaner: DataCleaner,
                     private val sessionManager: SessionManager,
                     private val appContext: Context) : FilesRootInteractor(filesRepository) {

    val filesRepositoryResolved: Single<Boolean> = Single.fromCallable {
        filesRepository.resolve()
    }

    val isP8: Boolean
        get() {

            val session = sessionManager.session
            return session != null && session.apiVersion == Const.ApiVersion.API_P8

        }

    val userLogin: Single<String>
        get() = Single.fromCallable {

            val session = sessionManager.session ?: throw IllegalStateException("Not authorized.")

            session.user

        }

    val networkState: Single<Boolean>
        get() = Single.fromCallable {

            val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as? ConnectivityManager ?: return@fromCallable false

            val activeNetwork = cm.activeNetworkInfo

            activeNetwork != null && activeNetwork.isConnectedOrConnecting

        }

    fun logout(): Completable = authenticatorService.logout()
            .andThen(dataCleaner.cleanAllUserData())
            .andThen(Completable.fromAction {
                appContext.stopService(FileObserverService.intent(appContext))
                AppUtil.setComponentEnabled(
                        FileObserverService::class.java, false, appContext)
            })

}
