package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import androidx.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class TextViewBinding {

    @BindingAdapter("font")
    public static void setTextViewFont(TextView view, String path){
        Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(), path);
        view.setTypeface(typeface);
    }

    @BindingAdapter({"android:text", "defaultText"})
    public static void setCaption(TextView view, String text, String defaultCaption){
        if (text != null){
            view.setText(text);
        } else {
            view.setText(defaultCaption);
        }
    }

    @BindingAdapter({"android:hint", "defaultHint"})
    public static void setCaptionHint(TextView view, String caption, String defaultCaption){
        if (caption != null){
            view.setHint(caption);
        } else {
            view.setHint(defaultCaption);
        }
    }

}
