package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import android.text.Editable;
import android.text.TextWatcher;

import com.afterlogic.aurora.drive.R;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */
class StringBindingWatcher extends BindableListener<String> implements TextWatcher {

    public static final int TAG = R.id.bindable_stringBindingWatcher;
    private boolean ignoreChange = false;

    StringBindingWatcher(Bindable<String> field) {
        super(field);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        ignoreChange = true;
        set(editable.toString());
        ignoreChange = false;
    }

    public boolean isIgnoreChange() {
        return ignoreChange;
    }
}
