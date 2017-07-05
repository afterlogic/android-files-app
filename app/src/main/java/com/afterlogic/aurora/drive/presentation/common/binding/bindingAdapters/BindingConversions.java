package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import android.databinding.BindingConversion;

import com.afterlogic.aurora.drive.presentation.common.binding.binder.Bindable;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.BooleanBindable;

/**
 * Created by sashka on 05.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class BindingConversions {

    @BindingConversion
    public static boolean convertBooleanBinder(Bindable<Boolean> booleanBindable){
        return booleanBindable != null && booleanBindable.get();
    }

    @BindingConversion
    public static boolean convertBooleanBinder(BooleanBindable booleanBinder){
        return convertBooleanBinder(((Bindable<Boolean>) booleanBinder));
    }
}
