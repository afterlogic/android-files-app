package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import android.widget.CompoundButton;

import com.afterlogic.aurora.drive.R;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

class CompoundBindingListener extends BindableListener<Boolean> implements CompoundButton.OnCheckedChangeListener {

    public static final int TAG = R.id.bindable_compoundListener;

    CompoundBindingListener(Bindable<Boolean> field) {
        super(field);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        set(b);
    }
}
