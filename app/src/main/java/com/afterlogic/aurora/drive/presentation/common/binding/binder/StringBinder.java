package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.interfaces.Provider;

/**
 * Created by sashka on 23.12.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class StringBinder extends Binder<String> implements TextWatcher{

    private boolean mTextInputMode = false;

    public StringBinder() {
    }

    public StringBinder(@NonNull Provider<String> get, @NonNull Consumer<String> set) {
        super(get, set);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public synchronized void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mTextInputMode = true;
        String value = charSequence.toString();
        set(value);
        mTextInputMode = false;
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public synchronized void set(String value) {
        super.set(value);
    }

    @Override
    public synchronized void notifyChange() {
        if (mTextInputMode) return;
        super.notifyChange();
    }
}
