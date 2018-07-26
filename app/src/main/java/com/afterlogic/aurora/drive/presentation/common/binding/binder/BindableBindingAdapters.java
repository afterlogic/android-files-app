package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import androidx.databinding.BindingAdapter;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by sashka on 23.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BindableBindingAdapters {

    @BindingAdapter("bindableChecked")
    public static void bindCompoundBoolean(CompoundButton checkBox, Bindable<Boolean> field) {

        CompoundBindingListener prev = (CompoundBindingListener) checkBox.getTag(CompoundBindingListener.TAG);

        if (prev == null || prev.getField() != field) {

            if (prev != null) {
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setTag(CompoundBindingListener.TAG, null);
            }

            if (field != null) {
                CompoundBindingListener listener = new CompoundBindingListener(field);
                checkBox.setTag(CompoundBindingListener.TAG, listener);
                checkBox.setOnCheckedChangeListener(listener);
            }
        }

        if (field != null) {
            checkBox.setChecked(field.get());
        }
    }

    @BindingAdapter("bindableSelectedByClick")
    public static void bindTextViewText(View view, Bindable<Boolean> field) {

        SelectedByClickListener prev = (SelectedByClickListener) view.getTag(SelectedByClickListener.TAG);

        if (prev == null || prev.getField() != field) {

            if (prev != null) {
                view.setOnClickListener(null);
                view.setTag(SelectedByClickListener.TAG, null);
            }

            if (field != null) {
                SelectedByClickListener listener = new SelectedByClickListener(field);
                view.setTag(SelectedByClickListener.TAG, listener);
                view.setOnClickListener(listener);
            }
        }

        if (field != null) {
            view.setSelected(field.get());
        }
    }

    @BindingAdapter("bindableText")
    public static void setTextListener(EditText view, Bindable<String> field){

        StringBindingWatcher current = (StringBindingWatcher) view.getTag(StringBindingWatcher.TAG);

        if (current == null || current.getField() != field){

            if (current != null) {
                view.removeTextChangedListener(current);
                view.setTag(StringBindingWatcher.TAG, null);
                current = null;
            }

            if (field != null) {
                current = new StringBindingWatcher(field);
                view.addTextChangedListener(current);
                view.setTag(StringBindingWatcher.TAG, current);
            }

        }

        if (current != null && !current.isIgnoreChange()) {

            int currentSelection = view.getSelectionEnd();
            String text = field.get();
            int textLength = text != null ? text.length() : 0;

            view.setText(text);
            view.setSelection(Math.min(textLength, currentSelection));

        }
    }
}
