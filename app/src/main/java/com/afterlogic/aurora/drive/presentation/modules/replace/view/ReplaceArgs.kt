package com.afterlogic.aurora.drive.presentation.modules.replace.view

import android.os.Bundle

import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.Args

import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceArgs : Args {

    var isCopyMode: Boolean
        get() = bundle.getBoolean(KEY_COPY_MODE, false)
        set(copyMode) = args.putBoolean(KEY_COPY_MODE, copyMode)

    var files: List<AuroraFile>
        get() = bundle.getParcelableArrayList(KEY_FILES)
        set(files) = args.putParcelableArrayList(KEY_FILES, ArrayList(files))

    constructor(args: Bundle) : super(args)

    constructor() : super()

    companion object {

        private val KEY_COPY_MODE = ReplaceArgs::class.java.name + ".copyMode"
        private val KEY_FILES = ReplaceArgs::class.java.name + ".files"
    }

    internal class Initializer @Inject constructor() {

        lateinit var args: ReplaceArgs

    }

}
