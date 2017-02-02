package com.afterlogic.aurora.drive.presentation.common.binding.binder.common;

import android.databinding.BindingConversion;

import com.afterlogic.aurora.drive.presentation.common.binding.binder.BooleanBinder;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.StringBinder;

/**
 * Created by sashka on 23.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BinderBindingConversion {

    @BindingConversion
    public static String convertString(StringBinder binder){
        return binder.get();
    }

    @BindingConversion
    public static boolean convertBoolean(BooleanBinder binder){
        return binder.get();
    }
}
