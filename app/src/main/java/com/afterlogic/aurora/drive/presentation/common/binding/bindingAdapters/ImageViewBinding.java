package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import androidx.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ImageViewBinding {

    @BindingAdapter("imageResource")
    public static void bindImageResource(ImageView imageView, int resourceId) {

        if (resourceId != View.NO_ID) {

            imageView.setImageResource(resourceId);

        } else {

            imageView.setImageDrawable(null);

        }

    }

    @BindingAdapter("imageUri")
    public static void setImageFromUri(ImageView imageView, Uri uri) {

        setImageFromUri(imageView, uri, null);

    }

    @BindingAdapter({"imageUri", "defaultImageUri"})
    public static void setImageFromUriWithDefault(ImageView imageView, Uri uri, Uri defaultUri) {

        if (uri == null) {

            setImageFromUri(imageView, defaultUri);

        } else {

            setImageFromUri(imageView, uri, new RequestListener<Drawable>() {

                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                    imageView.post(() -> setImageFromUri(imageView, defaultUri));

                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }

            });

        }

    }

    @BindingAdapter({"imageUri", "glideListener"})
    public static void setImageFromUri(ImageView imageView, Uri uri, @Nullable RequestListener<Drawable> listener) {
        RequestManager glide = Glide.with(imageView.getContext());

        glide.clear(imageView);

        if (uri != null) {

            RequestBuilder<Drawable> request;

            if (uri.getScheme().startsWith("http")) {

                GlideUrl url = new GlideUrl(uri.toString());
                request = glide.load(url);

            } else {

                request = glide.load(uri);

            }

            if (listener != null){
                request = request.listener(listener);
            }

            request.into(imageView);

        } else {

            imageView.setImageDrawable(null);

        }
    }

    @BindingAdapter("zoomable")
    public static void bindPhotoViewZoomable(PhotoView view, boolean scallable) {

        view.setZoomable(scallable);

    }

    @BindingAdapter("scale")
    public static void bindPhotoViewScale(PhotoView view, float scale) {

        if (scale > 0){

            view.setScale(scale);

        }

    }

    @BindingAdapter("onTap")
    public static void bindOnTap(PhotoView view, OnPhotoTapListener listener) {

        view.setOnPhotoTapListener(listener);

    }

    @BindingAdapter("tintColor")
    public static void bindTintColor(ImageView view, int color) {

        if (color != -1) {

            view.setColorFilter(color);

        } else {

            view.clearColorFilter();

        }

    }
}
