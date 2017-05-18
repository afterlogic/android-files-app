package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.widget.EditText;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class EditTextBinding {

    @BindingAdapter("error")
    public static void bindError(EditText editText, @Nullable String error){
        editText.setError(error);
        if (error != null){
            editText.requestFocus();
        }
    }
}
