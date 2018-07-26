package com.afterlogic.aurora.drive.data.common.multiapi

import com.afterlogic.aurora.drive.core.common.util.tryOptional
import com.afterlogic.aurora.drive.data.common.annotations.P7
import com.afterlogic.aurora.drive.data.common.annotations.P8
import com.afterlogic.aurora.drive.data.common.network.SessionManager
import io.reactivex.*
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */

class MultiApiService<T> @Inject constructor(
        private val sessionManager: SessionManager,
        @P7 private val p7Provider: Provider<T>,
        @P8 private val p8Provider: Provider<T>
) {

    private val service by lazy {
        MultiApiUtil.chooseByApiVersion(sessionManager, p7Provider, p8Provider)
    }

    fun resolve(): Boolean = tryOptional { service } != null

    @Throws(MultiApiServiceError::class)
    fun execute(action: (T).() -> Unit) {
        action(service)
    }

    fun completable(action: (T).() -> Completable): Completable = Completable.defer {
        action(service)
    }

    fun <R> maybe(action: (T).() -> Maybe<R>): Maybe<R> = Maybe.defer {
        action(service)
    }

    fun <R> single(action: (T).() -> Single<R>): Single<R> = Single.defer {
        action(service)
    }

    fun <R> observable(action: (T).() -> Observable<R>): Observable<R> = Observable.defer {
        action(service)
    }

    fun <R> flowable(action: (T).() -> Flowable<R>): Flowable<R> = Flowable.defer {
        action(service)
    }

}