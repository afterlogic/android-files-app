package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.view.View;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFileItemViewModel extends View.OnLongClickListener, View.OnClickListener{

    ObservableField<String> getFileName();

    ObservableField<Uri> getFileIcon();

    ObservableField<Uri> getStatusIcon();

    BaseFileItemModel getModel();

    ObservableInt getProgress();
}
