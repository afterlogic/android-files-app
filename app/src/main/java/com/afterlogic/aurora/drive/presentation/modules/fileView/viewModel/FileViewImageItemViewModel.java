package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.bumptech.glide.request.RequestListener;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewImageItemViewModel extends RequestListener<Drawable> {

    ObservableField<Uri> getImageContent();

    ObservableBoolean getProgress();

    ObservableBoolean getError();

    ObservableBoolean getFullscreen();

    void onViewStart();

    void onViewStop();

    void toggleFullscreen();
}
