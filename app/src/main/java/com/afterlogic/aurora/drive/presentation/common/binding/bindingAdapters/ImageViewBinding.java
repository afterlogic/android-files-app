package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ImageViewBinding {

    @BindingAdapter("bind:imageUri")
    public static void setImageFromUri(ImageView imageView, Uri uri){
        Glide.clear(imageView);
        if (uri != null) {
            Glide.with(imageView.getContext())
                    .fromUri()
                    .load(uri)
                    .into(imageView);
        } else {
            imageView.setImageDrawable(null);
        }
    }

    @BindingAdapter("bind:imageUrl")
    public static void setImageFromUrl(ImageView imageView, String url){
        setImageFromUri(imageView, Uri.parse(url));
    }
}
