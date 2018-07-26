package com.afterlogic.aurora.drive.data.common.multiapi

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */

open class MultiApiServiceError: RuntimeException()

class MultiApiServiceNotAuthorizedError: MultiApiServiceError()
class MultiApiServiceUnknownApiError: MultiApiServiceError()