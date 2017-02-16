package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.view.View;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewImageItemViewModel extends View.OnClickListener{

    void viewCreatedWith(AuroraFile file);

    ObservableField<Uri> getImageContent();

    ObservableBoolean getProgress();

    ObservableBoolean getError();

    ObservableBoolean getFullscreen();
}
