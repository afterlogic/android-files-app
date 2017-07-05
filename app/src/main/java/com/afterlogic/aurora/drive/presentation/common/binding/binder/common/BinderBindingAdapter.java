package com.afterlogic.aurora.drive.presentation.common.binding.binder.common;

import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.BooleanBindable;
import com.afterlogic.aurora.drive.presentation.common.binding.binder.StringBindable;

/**
 * Created by sashka on 23.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BinderBindingAdapter {

    @BindingAdapter("binder")
    public static void bindCompound(CompoundButton checkBox, BooleanBindable binder){
        checkBox.setOnCheckedChangeListener(binder);
        binder.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(binder.get());
                checkBox.setOnCheckedChangeListener(binder);
            }
        });
        checkBox.setChecked(binder.get());
    }

    @BindingAdapter("binder")
    public static void bindTextView(View textView, BooleanBindable binder){
        textView.setOnClickListener(view -> binder.set(!binder.get()));
        binder.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                textView.setSelected(binder.get());
            }
        });
        textView.setSelected(binder.get());
    }

    @BindingAdapter("visibility")
    public static void bindViewVisibility(View view, BooleanBindable binder){
        bindViewVisibility(view, binder, false);
    }

    @BindingAdapter({"visibilityBoolean", "inverseVisibility"})
    public static void bindViewVisibility(View view, BooleanBindable binder, boolean inverse){
        boolean value = binder.get();

        if (inverse) value = !value;

        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("binder")
    public static void setTextListener(TextView view, StringBindable binder){
        TextWatcher prev = (TextWatcher) view.getTag(R.id.bind_text_adapter);
        if (prev != null){
            view.removeTextChangedListener(prev);
        }
        view.addTextChangedListener(binder);
        view.setText(binder.get());
    }
}
