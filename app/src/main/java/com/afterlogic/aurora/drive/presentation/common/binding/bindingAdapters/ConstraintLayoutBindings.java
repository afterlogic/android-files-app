package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.support.constraint.ConstraintLayout;
import android.view.View;

/**
 * Created by sunny on 16.09.17.
 * mail: mail@sunnydaydev.me
 */

public class ConstraintLayoutBindings {

    @BindingAdapter("layout_constraintGuide_percent")
    public static void bindLayoutConstraintGuidePercent(View view, float value) {

        if (view.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {

            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            lp.guidePercent = value;

            view.requestLayout();

        }

    }
}
