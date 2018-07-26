package com.afterlogic.aurora.drive.data.common.multiapi

import com.afterlogic.aurora.drive.core.consts.Const
import com.afterlogic.aurora.drive.data.common.network.SessionManager

import javax.inject.Provider

/**
 * Created by sashka on 03.02.17.
 *
 *
 * mail: sunnyday.development@gmail.com
 */

object MultiApiUtil {

    @Throws(MultiApiServiceError::class)
    fun <T> chooseByApiVersion(sessionManager: SessionManager,
                               p7: Provider<T>, p8: Provider<T>): T {

        val session = sessionManager.session
                ?: throw MultiApiServiceNotAuthorizedError()

        return when (session.apiVersion) {
            Const.ApiVersion.API_P7 -> p7.get()
            Const.ApiVersion.API_P8 -> p8.get()
            else -> throw MultiApiServiceUnknownApiError()
        }

    }

}
