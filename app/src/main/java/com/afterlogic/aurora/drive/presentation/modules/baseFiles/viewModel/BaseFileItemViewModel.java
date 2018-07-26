package com.afterlogic.aurora.drive.presentation.modules.baseFiles.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import android.net.Uri;
import android.view.View;

import com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.BaseFileItemModel;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFileItemViewModel extends View.OnLongClickListener, View.OnClickListener {

    ObservableField<String> getFileName();

    ObservableField<Uri> getFileIcon();

    ObservableField<Uri> getStatusIcon();

    BaseFileItemModel getModel();

    ObservableBoolean getIsFolder();

}
