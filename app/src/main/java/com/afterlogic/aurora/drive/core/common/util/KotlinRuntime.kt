package com.afterlogic.aurora.drive.core.common.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */

fun <T> tryOptional(action: () -> T): T? {
    return try {
        action()
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}