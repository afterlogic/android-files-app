package com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.view.View;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewImageItemViewModel extends View.OnClickListener, PhotoViewAttacher.OnViewTapListener, RequestListener<Uri, GlideDrawable>{

    ObservableField<Uri> getImageContent();

    ObservableBoolean getProgress();

    ObservableBoolean getError();

    ObservableBoolean getFullscreen();

    void onViewStart();

    void onViewStop();
}
