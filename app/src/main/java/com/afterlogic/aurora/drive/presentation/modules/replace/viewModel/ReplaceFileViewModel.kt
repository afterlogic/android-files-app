package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel

import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.AuroraFileViewModel

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

class ReplaceFileViewModel(
        file: AuroraFile,
        onItemClickListener: OnActionListener<AuroraFile>
) : AuroraFileViewModel(file, onItemClickListener)
