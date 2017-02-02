package com.afterlogic.aurora.drive.presentation.common.binding;

import android.databinding.BindingConversion;

import com.afterlogic.aurora.drive.presentation.common.binding.binder.Binder;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.BooleanBinder;

/**
 * Created by sashka on 05.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class BindingConversions {

    @BindingConversion
    public static boolean convertBooleanBinder(Binder<Boolean> booleanBinder){
        return booleanBinder != null && booleanBinder.get();
    }

    @BindingConversion
    public static boolean convertBooleanBinder(BooleanBinder booleanBinder){
        return convertBooleanBinder(((Binder<Boolean>) booleanBinder));
    }
}
