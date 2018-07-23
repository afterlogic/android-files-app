package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import androidx.databinding.BindingAdapter;
import androidx.annotation.Nullable;
import android.widget.EditText;

import com.afterlogic.aurora.drive.presentation.common.binding.models.EditorEvent;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class EditTextBinding {

    @BindingAdapter("error")
    public static void bindError(EditText editText, @Nullable String error){
        editText.setError(error);
    }

    @BindingAdapter("onEditorEvent")
    public static void bindOnImeOption(EditText editText, EditorEventListener eventConsumer) {

        if (eventConsumer == null) {

            editText.setOnEditorActionListener(null);
            return;

        }

        editText.setOnEditorActionListener((view, id, keyEvent) ->
                eventConsumer.onEditorEvent(new EditorEvent(id, keyEvent))
        );

    }

    public interface EditorEventListener {

        boolean onEditorEvent(EditorEvent event);

    }

}
