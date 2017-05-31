package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ImageViewBinding {

    @BindingAdapter("imageUri")
    public static void setImageFromUri(ImageView imageView, Uri uri){
        setImageFromUri(imageView, uri, null);
    }

    @BindingAdapter({"imageUri", "glideListener"})
    public static void setImageFromUri(ImageView imageView, Uri uri, @Nullable RequestListener<Uri, GlideDrawable> listener){
        Glide.clear(imageView);
        if (uri != null) {

            if (uri.getScheme().startsWith("http")) {
                GlideUrl url = new GlideUrl(uri.toString());
                Glide.with(imageView.getContext())
                        .load(url);
            }

            DrawableRequestBuilder<Uri> request = Glide.with(imageView.getContext())
                    .fromUri()
                    .load(uri);

            if (listener != null){
                request = request.listener(listener);
            }

            request.into(imageView);
        } else {
            imageView.setImageDrawable(null);
        }
    }

    @BindingAdapter("zoomable")
    public static void bindPhotoViewZoomable(PhotoView view, boolean scallable){
        view.setZoomable(scallable);
    }

    @BindingAdapter("scale")
    public static void bindPhotoViewScale(PhotoView view, float scale){
        if (scale > 0){
            view.setScale(scale);
        }
    }

    @BindingAdapter("onTap")
    public static void bindOnTap(PhotoView view, PhotoViewAttacher.OnViewTapListener listener){
        view.setOnViewTapListener(listener);
    }

    @BindingAdapter("tintColor")
    public static void bindTintColor(ImageView view, int color){
        if (color != -1) {
            view.setColorFilter(color);
        } else {
            view.clearColorFilter();
        }
    }
}
