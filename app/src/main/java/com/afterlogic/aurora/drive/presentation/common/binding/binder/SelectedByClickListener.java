package com.afterlogic.aurora.drive.presentation.common.binding.binder;

import android.view.View;

import com.afterlogic.aurora.drive.R;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

class SelectedByClickListener extends BindableListener<Boolean> implements View.OnClickListener {

    public static final int TAG = R.id.bindable_compoundListener;

    SelectedByClickListener(Bindable<Boolean> field) {
        super(field);
    }

    @Override
    public void onClick(View view) {
        set(!getField().get());
    }
}
