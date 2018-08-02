package com.afterlogic.aurora.drive.presentation.modules.main.viewModel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import android.net.Uri

import com.afterlogic.aurora.drive.R
import com.afterlogic.aurora.drive.core.common.util.FileUtil
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.AuroraFileViewModel

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

class MainFileViewModel internal constructor(
        private val file: AuroraFile,
        onItemClickListener: OnActionListener<AuroraFile>,
        private val onLongClickListener: OnActionListener<AuroraFile>,
        private val appResources: AppResources
) : AuroraFileViewModel(file, onItemClickListener) {

    val icon = ObservableField<Uri>()
    val statusIcon = ObservableField<Uri>()
    val isFolder = ObservableBoolean()
    val selected = ObservableBoolean()
    val syncProgress = ObservableInt(-1)
    val isOffline = ObservableBoolean()
    val shared = ObservableBoolean()

    private var thumbnail: Uri? = null

    init {

        SimpleOnPropertyChangedCallback.addTo(Runnable { this.onOfflineStatusChanged() }, isOffline, syncProgress)
        isOffline.notifyChange()

        updateValues()
        setThumbnail(null)
    }

    fun onLongClick() {
        onLongClickListener.onAction(file)
    }

    fun setThumbnail(thumb: Uri?) {

        thumbnail = if (file.isFolder) appResources.getResourceUri(R.drawable.ic_folder)
                    else thumb ?: appResources.getResourceUri(FileUtil.getFileIconRes(file))

        if (thumbnail != icon.get()) {
            icon.set(thumbnail)
        }

    }

    private fun updateValues() {

        shared.set(file.isShared)
        isFolder.set(file.isFolder)

        setThumbnail(thumbnail)

    }

    private fun onOfflineStatusChanged() {

        val statusIcon: Uri? = if (isOffline.get()) {
            if (syncProgress.get() != -1) {
                appResources.getResourceUri(R.drawable.ic_sync)
            } else {
                appResources.getResourceUri(R.drawable.ic_offline)
            }
        } else {
            null
        }

        if (statusIcon != this.statusIcon.get()) {
            this.statusIcon.set(statusIcon)
        }

    }

}
