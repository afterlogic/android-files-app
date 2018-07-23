package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import androidx.databinding.BindingAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.RatingBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class RatingBarBinding {

    @BindingAdapter("rating")
    public static void bindRatingBarRate(@NonNull RatingBar ratingBar, @Nullable Bindable<Float> bindable){
        if (ratingBar.getTag(R.id.bind_target) != bindable) {
            if (bindable == null){
                ratingBar.setOnRatingBarChangeListener(null);
                ratingBar.setRating(0);
            } else {
                ratingBar.setTag(R.id.bind_target, bindable);
                ratingBar.setOnRatingBarChangeListener((ratingBar1, v, b) -> bindable.set(v));
            }
        }
        if (bindable != null) {
            ratingBar.setRating(bindable.get());
        }
    }
}
