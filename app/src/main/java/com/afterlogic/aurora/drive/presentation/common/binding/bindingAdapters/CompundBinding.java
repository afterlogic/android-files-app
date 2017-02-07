package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.widget.CompoundButton;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class CompundBinding {

    @BindingAdapter("bind:onCheck")
    public static void setOnCheckedChaneListener(CompoundButton checkBox, CompoundButton.OnCheckedChangeListener listener){
        checkBox.setOnCheckedChangeListener(listener);
    }
}
