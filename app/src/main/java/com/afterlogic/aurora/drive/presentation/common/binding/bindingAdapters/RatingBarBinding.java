package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RatingBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Binder;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class RatingBarBinding {

    @BindingAdapter("bind:rating")
    public static void bindRatingBarRate(@NonNull RatingBar ratingBar, @Nullable Binder<Float> binder){
        if (ratingBar.getTag(R.id.bind_target) != binder) {
            if (binder == null){
                ratingBar.setOnRatingBarChangeListener(null);
                ratingBar.setRating(0);
            } else {
                ratingBar.setTag(R.id.bind_target, binder);
                ratingBar.setOnRatingBarChangeListener((ratingBar1, v, b) -> binder.set(v));
            }
        }
        if (binder != null) {
            ratingBar.setRating(binder.get());
        }
    }
}
