package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import androidx.databinding.BindingConversion;

/**
 * Created by sashka on 23.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BindableBindingConversion {

    @BindingConversion
    public static String convertString(Bindable<String> binder){
        return binder != null ? binder.get() : null;
    }

    @BindingConversion
    public static boolean convertBoolean(Bindable<Boolean> binder){
        return binder != null ? binder.get() : false;
    }
}
