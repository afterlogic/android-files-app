package com.afterlogic.aurora.drive.presentation.modules.main.viewModel

import androidx.annotation.Nullable

import com.afterlogic.aurora.drive.data.modules.appResources.AppResources
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener

import java.util.HashMap

import javax.inject.Inject

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

internal class FilesMapper @Inject
constructor(private val appResources: AppResources) {

    private val byFileMap = HashMap<AuroraFile, MainFileViewModel>()
    private val byFileSpecMap = HashMap<String, MainFileViewModel>()

    private var onLongClickListener: OnActionListener<AuroraFile>? = null

    val keys: Set<AuroraFile>
        get() = byFileMap.keys

    fun setOnLongClickListener(onLongClickListener: OnActionListener<AuroraFile>) {
        this.onLongClickListener = onLongClickListener
    }

    fun mapAndStore(file: AuroraFile, onItemClickListener: OnActionListener<AuroraFile>): MainFileViewModel {
        val vm = MainFileViewModel(file, onItemClickListener, onLongClickListener!!, appResources)
        byFileMap[file] = vm
        byFileSpecMap[file.pathSpec] = vm
        return vm
    }

    @Nullable
    operator fun get(file: AuroraFile): MainFileViewModel? {
        return byFileMap[file]
    }

    @Nullable
    operator fun get(fileSpec: String): MainFileViewModel? {
        return byFileSpecMap[fileSpec]
    }

    @Nullable
    fun remove(file: AuroraFile): MainFileViewModel? {
        byFileSpecMap.remove(file.pathSpec)
        return byFileMap.remove(file)
    }

    fun clear() {
        byFileMap.clear()
        byFileSpecMap.clear()
    }

}
