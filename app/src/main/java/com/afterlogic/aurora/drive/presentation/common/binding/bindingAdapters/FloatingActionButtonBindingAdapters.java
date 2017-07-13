package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;

import com.afterlogic.aurora.drive.presentation.common.view.FloatingActionButton;

/**
 * Created by aleksandrcikin on 14.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FloatingActionButtonBindingAdapters {

    @BindingAdapter("autoCollapseMenu")
    public static void bindAutoCollapseMenu(FloatingActionButton button, boolean autoCollapse) {
        button.setAutoCollapseMenu(autoCollapse);
    }
}
