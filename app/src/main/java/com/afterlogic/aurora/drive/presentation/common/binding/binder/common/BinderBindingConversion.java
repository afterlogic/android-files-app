package com.afterlogic.aurora.drive.presentation.common.binding.binder.common;

import android.databinding.BindingConversion;

import com.afterlogic.aurora.drive.presentation.common.binding.binder.BooleanBindable;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.StringBindable;

/**
 * Created by sashka on 23.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BinderBindingConversion {

    @BindingConversion
    public static String convertString(StringBindable binder){
        return binder.get();
    }

    @BindingConversion
    public static boolean convertBoolean(BooleanBindable binder){
        return binder.get();
    }
}
